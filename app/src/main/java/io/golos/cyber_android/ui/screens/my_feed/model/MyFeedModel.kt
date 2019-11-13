package io.golos.cyber_android.ui.screens.my_feed.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.posts.GetPostsUseCase

interface MyFeedModel : ModelBase, GetPostsUseCase {
}