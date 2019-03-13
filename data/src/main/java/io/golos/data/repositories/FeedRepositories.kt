package io.golos.data.repositories

import io.golos.cyber4j.model.CyberDiscussion
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.DiscussionTimeSort
import io.golos.cyber4j.model.DiscussionsResult
import io.golos.data.CommentsApiService
import io.golos.data.PostsApiService
import io.golos.domain.entities.CommentEntity
import io.golos.domain.entities.DiscussionId
import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity
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
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<PostEntity>>,
    mainDispatcher: CoroutineDispatcher,
    workerDispatcher: CoroutineDispatcher,
    logger: Logger
) :
    AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>(
        feedMapper,
        postMapper, postMerger, feedMerger, emptyFeedProducer, mainDispatcher, workerDispatcher, logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionId): CyberDiscussion {
        return apiService.getPost(CyberName(params.userId), params.permlink, params.refBlockNum)
    }

    override suspend fun getFeedOnBackground(updateRequest: PostFeedUpdateRequest): DiscussionsResult {
        return when (updateRequest) {
            is CommunityFeedUpdateRequest -> apiService.getCommunityPosts(
                updateRequest.communityId,
                updateRequest.limit,
                when (updateRequest.sort) {
                    DiscussionsSort.FROM_OLD_TO_NEW -> DiscussionTimeSort.INVERTED
                    DiscussionsSort.FROM_NEW_TO_OLD -> DiscussionTimeSort.SEQUENTIALLY
                },
                updateRequest.sequenceKey
            )
        }
    }
}

class CommentsFeedRepository(
    private val apiService: CommentsApiService,
    feedMapper: CyberToEntityMapper<FeedUpdateRequestsWithResult<FeedUpdateRequest>, FeedEntity<CommentEntity>>,
    postMapper: CyberToEntityMapper<CyberDiscussion, CommentEntity>,
    postMerger: EntityMerger<CommentEntity>,
    feedMerger: EntityMerger<FeedEntity<CommentEntity>>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<CommentEntity>>,
    mainDispatcher: CoroutineDispatcher,
    workerDispatcher: CoroutineDispatcher,
    logger: Logger
) :
    AbstractDiscussionsRepository<CommentEntity, CommentFeedpdateRequest>(
        feedMapper,
        postMapper, postMerger, feedMerger, emptyFeedProducer, mainDispatcher, workerDispatcher, logger
    ) {

    override suspend fun getDiscussionItem(params: DiscussionId): CyberDiscussion {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFeedOnBackground(updateRequest: CommentFeedpdateRequest): DiscussionsResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}