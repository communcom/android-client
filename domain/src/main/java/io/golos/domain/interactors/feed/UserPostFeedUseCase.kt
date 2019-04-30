package io.golos.domain.interactors.feed

import io.golos.domain.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.DiscussionsFeed
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.UserPostsUpdateRequest
import io.golos.domain.rules.EntityToModelMapper

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class UserPostFeedUseCase(
    private val userId: CyberUser,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: EntityToModelMapper<FeedRelatedEntities<PostEntity>, DiscussionsFeed<PostModel>>,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>(
    postFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {


    override val baseFeedUpdateRequest: UserPostsUpdateRequest
            by lazy { UserPostsUpdateRequest(userId.userId, 0, DiscussionsSort.FROM_NEW_TO_OLD, null) }

    override fun requestFeedUpdate(limit: Int, option: UpdateOption) {
        discussionsFeedRepository.makeAction(
            UserPostsUpdateRequest(
                userId.userId,
                limit,
                DiscussionsSort.FROM_NEW_TO_OLD,
                when (option.resolveUpdateOption()) {
                    UpdateOption.REFRESH_FROM_BEGINNING -> null
                    UpdateOption.FETCH_NEXT_PAGE -> discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value?.nextPageId
                }
            )
        )
    }
}