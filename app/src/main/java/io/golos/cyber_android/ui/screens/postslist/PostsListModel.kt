package io.golos.cyber_android.ui.screens.postslist

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.interactors.posts.GetPostsUseCase

interface PostsListModel : ModelBase, GetPostsUseCase {
}