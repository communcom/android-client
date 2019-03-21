package io.golos.data.repositories

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.DiscussionTimeSort
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.data.api.CommentsApiService
import io.golos.data.api.PostsApiService
import io.golos.domain.Logger
import io.golos.domain.entities.*
import io.golos.domain.model.*
import io.golos.domain.rules.*
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class PostsFeedRepository(
    private val apiService: PostsApiService,
    feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<PostEntity>>,
    postMapper: CyberToEntityMapper<CyberDiscussion, PostEntity>,
    postMerger: EntityMerger<PostEntity>,
    feedMerger: EntityMerger<FeedEntity<PostEntity>>,
    feedUpdateApprover: RequestApprover<PostFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<PostEntity>>,
    mainDispatcher: CoroutineDispatcher,
    workerDispatcher: CoroutineDispatcher,
    logger: Logger
) :
    AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>(
        feedMapper,
        postMapper,
        postMerger,
        feedMerger,
        feedUpdateApprover,
        emptyFeedProducer,
        mainDispatcher,
        workerDispatcher,
        logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussion {
        return apiService.getPost(CyberName(params.userId), params.permlink, params.refBlockNum)
    }

    override suspend fun getFeedOnBackground(updateRequest: PostFeedUpdateRequest): DiscussionsResult {
        return when (updateRequest) {
            is CommunityFeedUpdateRequest -> apiService.getCommunityPosts(
                updateRequest.communityId,
                updateRequest.limit,
                updateRequest.sort.toDiscussionSort(),
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

    private fun DiscussionsSort.toDiscussionSort() = when (this) {
        DiscussionsSort.FROM_OLD_TO_NEW -> DiscussionTimeSort.INVERTED
        DiscussionsSort.FROM_NEW_TO_OLD -> DiscussionTimeSort.SEQUENTIALLY
    }
}

class CommentsFeedRepository(
    private val apiService: CommentsApiService,
    feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>>,
    postMapper: CyberToEntityMapper<CyberDiscussion, CommentEntity>,
    postMerger: EntityMerger<CommentEntity>,
    feedMerger: EntityMerger<FeedEntity<CommentEntity>>,
    approver: RequestApprover<CommentFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<CommentEntity>>,
    mainDispatcher: CoroutineDispatcher,
    workerDispatcher: CoroutineDispatcher,
    logger: Logger
) :
    AbstractDiscussionsRepository<CommentEntity, CommentFeedUpdateRequest>(
        feedMapper,
        postMapper, postMerger, feedMerger, approver, emptyFeedProducer, mainDispatcher, workerDispatcher, logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussion {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFeedOnBackground(updateRequest: CommentFeedUpdateRequest): DiscussionsResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}