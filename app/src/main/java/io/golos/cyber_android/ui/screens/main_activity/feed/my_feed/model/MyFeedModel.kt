package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase

interface MyFeedModel : ModelBase,
    GetPostsUseCase,
    GetLocalUserUseCase{
}