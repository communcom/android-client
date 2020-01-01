package io.golos.cyber_android.ui.screens.community_page_post.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
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

    suspend fun upVote(communityId: String, userId: String, permlink: String)

    suspend fun downVote(communityId: String, userId: String, permlink: String)

    suspend fun reportPost(authorPostId: String, communityId: String, permlink: String, reason: String)

}