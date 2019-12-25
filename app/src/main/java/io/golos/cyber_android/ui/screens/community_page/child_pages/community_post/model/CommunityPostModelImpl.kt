package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model

import io.golos.domain.use_cases.posts.GetPostsUseCase
import javax.inject.Inject

class CommunityPostModelImpl @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : CommunityPostModel,
    GetPostsUseCase by getPostsUseCase