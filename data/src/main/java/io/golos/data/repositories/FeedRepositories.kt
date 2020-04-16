package io.golos.data.repositories

import io.golos.commun4j.model.CyberDiscussionRaw
import io.golos.commun4j.model.GetDiscussionsResultRaw
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.*
import io.golos.domain.mappers.CyberCommentsToEntityMapper
import io.golos.domain.mappers.CyberFeedToEntityMapper
import io.golos.domain.mappers.CyberPostToEntityMapper
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.CommentsOfApPostUpdateRequest
import io.golos.domain.requestmodel.CommunityFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.rules.EmptyEntityProducer
import io.golos.domain.rules.EntityMerger
import io.golos.domain.rules.RequestApprover
import io.golos.domain.use_cases.model.FeedTimeFrameOption
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
@ApplicationScope
class PostsFeedRepository
@Inject
constructor(
//    private val apiService: DiscussionsApi,
    feedMapper: CyberFeedToEntityMapper,
    postMapper: CyberPostToEntityMapper,
    postMerger: EntityMerger<PostEntity>,
    feedMerger: EntityMerger<FeedRelatedData<PostEntity>>,
    feedUpdateApprover: RequestApprover<PostFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<PostEntity>>,
    dispatchersProvider: DispatchersProvider
) : AbstractDiscussionsRepository<PostEntity, PostFeedUpdateRequest>(
        feedMapper,
        postMapper,
        postMerger,
        feedMerger,
        feedUpdateApprover,
        emptyFeedProducer,
        dispatchersProvider
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussionRaw {
        throw UnsupportedOperationException("")
        //return apiService.getPost(CyberName(params.userId), params.permlink)
    }

    override fun fixOnPositionDiscussion(discussion: PostEntity, parent: DiscussionIdEntity) {
        //for now works only on comments
        throw UnsupportedOperationException()
    }

    override suspend fun getFeedOnBackground(updateRequest: PostFeedUpdateRequest): GetDiscussionsResultRaw {
        throw UnsupportedOperationException("")
    }

    override val allDataRequest: PostFeedUpdateRequest =
        CommunityFeedUpdateRequest("stub", 0, DiscussionsSort.FROM_NEW_TO_OLD, FeedTimeFrameOption.ALL, "stub")
}

@ApplicationScope
class CommentsFeedRepository
@Inject
constructor(
    feedMapper: CyberCommentsToEntityMapper,
    postMerger: EntityMerger<CommentEntity>,
    feedMerger: EntityMerger<FeedRelatedData<CommentEntity>>,
    approver: RequestApprover<CommentFeedUpdateRequest>,
    emptyFeedProducer: EmptyEntityProducer<FeedEntity<CommentEntity>>,
    dispatchersProvider: DispatchersProvider
) : AbstractDiscussionsRepository<CommentEntity, CommentFeedUpdateRequest>(
        feedMapper,
        null/*postMapper*/,
        postMerger,
        feedMerger,
        approver,
        emptyFeedProducer,
        dispatchersProvider
    ) {

    override suspend fun getDiscussionItem(params: DiscussionIdEntity): CyberDiscussionRaw {
        throw UnsupportedOperationException("")
    }

    override fun fixOnPositionDiscussion(discussion: CommentEntity, parent: DiscussionIdEntity) {
        throw UnsupportedOperationException("")
    }

    override suspend fun getFeedOnBackground(updateRequest: CommentFeedUpdateRequest): GetDiscussionsResultRaw {
        throw UnsupportedOperationException("")
    }

    override val allDataRequest: CommentFeedUpdateRequest =
        CommentsOfApPostUpdateRequest("stub", Permlink("stub"), 0, DiscussionsSort.FROM_NEW_TO_OLD, "stub")
}