package io.golos.data.repositories

import io.golos.commun4j.model.CyberDiscussion
import io.golos.commun4j.model.DiscussionsResult
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.CommentsApiService
import io.golos.data.api.PostsApiService
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.FeedTimeFrameOption
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
@ApplicationScope
class PostsFeedRepository
@Inject
constructor(
    private val apiService: PostsApiService,
    feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>>,
    postMapper: CyberToEntityMapper<CyberDiscussion, PostEntity>,
    postMerger: EntityMerger<PostEntity>,
    feedMerger: EntityMerger<FeedRelatedData<PostEntity>>,
    feedUpdateApprover: RequestApprover<PostFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<PostEntity>>,
    dispatchersProvider: DispatchersProvider,
    logger: Logger
) : AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>(
        feedMapper,
        postMapper,
        postMerger,
        feedMerger,
        feedUpdateApprover,
        emptyFeedProducer,
        dispatchersProvider,
        logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussion {
        return apiService.getPost(CyberName(params.userId), params.permlink)
    }

    override fun fixOnPositionDiscussion(discussion: PostEntity, parent: DiscussionIdEntity) {
        //for now works only on comments
        throw UnsupportedOperationException()
    }

    override suspend fun getFeedOnBackground(updateRequest: PostFeedUpdateRequest): DiscussionsResult {
        return when (updateRequest) {
            is CommunityFeedUpdateRequest -> apiService.getCommunityPosts(
                updateRequest.communityId,
                updateRequest.limit,
                updateRequest.sort.toDiscussionSort(),
                updateRequest.timeFrameOption.toFeedTimeFrame(),
                updateRequest.sequenceKey
            )
            is UserSubscriptionsFeedUpdateRequest -> apiService.getUserSubscriptions(
                updateRequest.userId,
                updateRequest.limit,
                updateRequest.sort.toDiscussionSort(),
                updateRequest.sequenceKey
            )
            is UserPostsUpdateRequest -> apiService.getUserPost(
                updateRequest.userId,
                updateRequest.limit,
                updateRequest.sort.toDiscussionSort(),
                updateRequest.sequenceKey
            )
        }
    }

    override val allDataRequest: PostFeedUpdateRequest =
        CommunityFeedUpdateRequest("stub", 0, DiscussionsSort.FROM_NEW_TO_OLD, FeedTimeFrameOption.ALL, "stub")
}

@ApplicationScope
class CommentsFeedRepository
@Inject
constructor(
    private val apiService: CommentsApiService,
    feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>>,
    postMapper: CyberToEntityMapper<CyberDiscussion, CommentEntity>,
    postMerger: EntityMerger<CommentEntity>,
    feedMerger: EntityMerger<FeedRelatedData<CommentEntity>>,
    approver: RequestApprover<CommentFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<CommentEntity>>,
    dispatchersProvider: DispatchersProvider,
    logger: Logger
) :
    AbstractDiscussionsRepository<CommentEntity, CommentFeedUpdateRequest>(
        feedMapper,
        postMapper,
        postMerger,
        feedMerger,
        approver,
        emptyFeedProducer,
        dispatchersProvider,
        logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussion {
        return apiService.getComment(CyberName(params.userId), params.permlink)
    }

    override fun fixOnPositionDiscussion(discussion: CommentEntity, parent: DiscussionIdEntity) {


        val commentsToAPosts = discussionsFeedMap
            .filterKeys { id -> id is CommentsOfApPostUpdateRequest.Id }
            .mapKeys { mapEntry ->
                mapEntry.key as CommentsOfApPostUpdateRequest.Id
            }

        commentsToAPosts.filter { mapEntry ->
            mapEntry.key._permlink == parent.permlink
                    && mapEntry.key._user == parent.userId
        }
            .onEach {
                fixedDiscussions[it.key] = (fixedDiscussions[it.key] ?: emptySet()).toMutableSet().apply {
                    add(discussion)
                }

            }
            .values
            .forEach { mutableLiveData ->
                val commentsFeed = mutableLiveData.value ?: return@forEach

                mutableLiveData.value = commentsFeed.copy(
                    discussions = listOf(discussion) + commentsFeed.discussions
                )
            }


        commentsToAPosts
            .filter { mapEntry ->
                mapEntry.value.value?.discussions.orEmpty().any { comment ->
                    comment.contentId == parent
                }
            }
            .onEach {
                fixedDiscussions[it.key] = (fixedDiscussions[it.key] ?: emptySet()).toMutableSet().apply {
                    add(discussion)
                }
            }
            .values
            .forEach { commentsLiveData ->
                val commentsFeed = commentsLiveData.value ?: return@forEach
                val commentsList = commentsFeed.discussions.toMutableList()
                when (val parentCommentPosition = commentsList.indexOfLast { it.contentId == parent }) {
                    -1 -> return@forEach
                    commentsList.lastIndex -> commentsList.add(discussion)
                    else -> commentsList.add(parentCommentPosition + 1, discussion)
                }

                commentsLiveData.value = commentsFeed.copy(discussions = commentsList)
            }

    }

    override suspend fun getFeedOnBackground(updateRequest: CommentFeedUpdateRequest): DiscussionsResult {
        return when (updateRequest) {
            is CommentsOfApPostUpdateRequest -> apiService.getCommentsOfPost(
                CyberName(updateRequest.user),
                updateRequest.permlink,
                updateRequest.limit,
                updateRequest.sort.toDiscussionSort(),
                updateRequest.sequenceKey
            )
        }
    }

    override val allDataRequest: CommentFeedUpdateRequest =
        CommentsOfApPostUpdateRequest("stub", "stub", 0, DiscussionsSort.FROM_NEW_TO_OLD, "stub")
}

internal fun DiscussionsSort.toDiscussionSort() = when (this) {
    DiscussionsSort.FROM_OLD_TO_NEW -> FeedSort.SEQUENTIALLY
    DiscussionsSort.FROM_NEW_TO_OLD -> FeedSort.INVERTED
    DiscussionsSort.POPULAR -> FeedSort.POPULAR
}

internal fun FeedTimeFrameOption.toFeedTimeFrame() = when (this) {
    FeedTimeFrameOption.DAY -> FeedTimeFrame.DAY
    FeedTimeFrameOption.WEEK -> FeedTimeFrame.WEEK
    FeedTimeFrameOption.MONTH -> FeedTimeFrame.MONTH
    FeedTimeFrameOption.YEAR -> FeedTimeFrame.YEAR
    FeedTimeFrameOption.ALL -> FeedTimeFrame.ALL
}