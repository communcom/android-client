package io.golos.domain.interactors.feed

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.CommentEntity
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.FeedRelatedEntities
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.CommentsOfApPostUpdateRequest
import io.golos.domain.rules.EntityToModelMapper

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-27.
 */
open class CommentsFeedUseCase(
    private val postId: DiscussionIdModel,
    commentsFeedRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: EntityToModelMapper<FeedRelatedEntities<CommentEntity>, DiscussionsFeed<CommentModel>>,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<CommentFeedUpdateRequest, CommentEntity, CommentModel>(
    commentsFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {


    override val baseFeedUpdateRequest: CommentsOfApPostUpdateRequest
            by lazy {
                CommentsOfApPostUpdateRequest(
                    postId.userId,
                    postId.permlink,
                    postId.refBlockNum,
                    0,
                    DiscussionsSort.FROM_NEW_TO_OLD,
                    null
                )
            }

    override fun requestFeedUpdate(limit: Int, option: UpdateOption) {
        val myFeed = discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value
        val nextPageId = myFeed?.nextPageId
        val resolvedOption = option.resolveUpdateOption()
        val request = CommentsOfApPostUpdateRequest(
            postId.userId,
            postId.permlink,
            postId.refBlockNum,
            limit,
            DiscussionsSort.FROM_NEW_TO_OLD,
            when (resolvedOption) {
                UpdateOption.REFRESH_FROM_BEGINNING -> null
                UpdateOption.FETCH_NEXT_PAGE -> nextPageId
            }
        )
        discussionsFeedRepository.makeAction(request)
    }
}