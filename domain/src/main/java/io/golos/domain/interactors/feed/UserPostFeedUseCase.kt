package io.golos.domain.interactors.feed

import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.entities.*
import io.golos.domain.interactors.model.FeedTimeFrameOption
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.UserPostsUpdateRequest
import io.golos.domain.mappers.PostFeedEntityToModelMapper
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class UserPostFeedUseCase
@Inject
constructor(
    private val userId: CyberUser,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: PostFeedEntityToModelMapper,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>(
    postFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
) {


    override val baseFeedUpdateRequest: UserPostsUpdateRequest
            by lazy { UserPostsUpdateRequest(userId.userId, 0, DiscussionsSort.FROM_NEW_TO_OLD, FeedTimeFrameOption.ALL, null) }

    override fun requestFeedUpdate(limit: Int,
                                   option: UpdateOption,
                                   sort: DiscussionsSort?,
                                   timeFrame: FeedTimeFrameOption?) {
        discussionsFeedRepository.makeAction(
            UserPostsUpdateRequest(
                userId.userId,
                limit,
                sort ?: DiscussionsSort.FROM_NEW_TO_OLD,
                timeFrame ?: FeedTimeFrameOption.ALL,
                when (option.resolveUpdateOption()) {
                    UpdateOption.REFRESH_FROM_BEGINNING -> null
                    UpdateOption.FETCH_NEXT_PAGE -> discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value?.nextPageId
                }
            )
        )
    }
}