package io.golos.domain.use_cases.feed

import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CyberUser
import io.golos.domain.dto.DiscussionsSort
import io.golos.domain.dto.PostEntity
import io.golos.domain.mappers.PostFeedEntityToModelMapper
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.UserPostsUpdateRequest
import io.golos.domain.use_cases.model.FeedTimeFrameOption
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.model.UpdateOption
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class UserPostFeedUseCase
@Inject
constructor(
    private val userId: CyberUser,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    feedMapper: PostFeedEntityToModelMapper,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>(
    postFeedRepository,
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