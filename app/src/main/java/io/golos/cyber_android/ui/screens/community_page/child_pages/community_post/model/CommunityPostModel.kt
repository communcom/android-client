package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase

interface CommunityPostModel : ModelBase,
    GetPostsUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase {

    suspend fun addToFavorite(permlink: String)

    suspend fun removeFromFavorite(permlink: String)

    suspend fun deletePost(permlink: String, communityId: String)

}