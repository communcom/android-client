package io.golos.cyber_android.ui.screens.posts_list.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.posts.GetPostsUseCase

interface PostsListModel : ModelBase, GetPostsUseCase {
}