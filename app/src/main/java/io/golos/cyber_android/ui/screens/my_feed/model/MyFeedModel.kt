package io.golos.cyber_android.ui.screens.my_feed.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.flow.Flow

interface MyFeedModel : ModelBase,
    GetPostsUseCase,
    GetLocalUserUseCase{

    val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>
}