package io.golos.cyber_android.ui.screens.my_feed.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getUserProfileUseCase: GetLocalUserUseCase,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val postFilter: PostFiltersHolder

) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase,
    GetLocalUserUseCase by getUserProfileUseCase,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override suspend fun deletePost(permlink: String) {
        delay(1000)
    }

    suspend override fun addToFavorite(permlink: String) {
        delay(1000)
    }

    suspend override fun removeFromFavorite(permlink: String) {
        delay(1000)
    }

    override val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
        get() = postFilter.feedFiltersFlow
}