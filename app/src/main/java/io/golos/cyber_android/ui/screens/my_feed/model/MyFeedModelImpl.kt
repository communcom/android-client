package io.golos.cyber_android.ui.screens.my_feed.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getUserProfileUseCase: GetLocalUserUseCase,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val postFilter: PostFiltersHolder,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider
) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase,
    GetLocalUserUseCase by getUserProfileUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override suspend fun deletePost(permlink: String) {
        delay(1000)
    }

    override suspend fun addToFavorite(permlink: String) {
        delay(1000)
    }

    override suspend fun removeFromFavorite(permlink: String) {
        delay(1000)
    }

    override suspend fun upVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(communityId, userId, permlink)
        }
    }

    override suspend fun downVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(communityId, userId, permlink)
        }
    }

    override val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
        get() = postFilter.feedFiltersFlow
}