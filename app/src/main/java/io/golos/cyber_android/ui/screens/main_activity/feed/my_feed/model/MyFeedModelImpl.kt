package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getUserProfileUseCase: GetLocalUserUseCase,
    private val postFilter: PostFiltersHolder
) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase,
    GetLocalUserUseCase by getUserProfileUseCase {

    override val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
        get() = postFilter.feedFiltersFlow
}