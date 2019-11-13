package io.golos.cyber_android.ui.screens.my_feed.model

import io.golos.domain.use_cases.posts.GetPostsUseCase
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(private val getPostsUseCase: GetPostsUseCase) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase {

}