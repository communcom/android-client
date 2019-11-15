package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.model

import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import javax.inject.Inject

class MyFeedModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val getUserProfileUseCase: GetLocalUserUseCase
) : MyFeedModel,
    GetPostsUseCase by getPostsUseCase,
    GetLocalUserUseCase by getUserProfileUseCase