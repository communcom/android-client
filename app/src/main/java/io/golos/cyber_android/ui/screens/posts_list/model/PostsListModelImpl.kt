package io.golos.cyber_android.ui.screens.posts_list.model

import io.golos.domain.use_cases.posts.GetPostsUseCase
import javax.inject.Inject

class PostsListModelImpl @Inject constructor(private val getPostsUseCase: GetPostsUseCase) : PostsListModel,
    GetPostsUseCase by getPostsUseCase {

}