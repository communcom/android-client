package io.golos.cyber_android.ui.screens.feed_my.model

import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import kotlinx.coroutines.flow.Flow

interface MyFeedModel : ModelBase,
    GetPostsUseCase,
    GetLocalUserUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase {

    suspend fun addToFavorite(permlink: String)

    suspend fun removeFromFavorite(permlink: String)

    suspend fun deletePost(permlink: String, communityId: CommunityIdDomain)

    suspend fun upVote(
        communityId: CommunityIdDomain,
        userId: String,
        permlink: String
    )

    suspend fun downVote(
        communityId: CommunityIdDomain,
        userId: String,
        permlink: String
    )

    suspend fun reportPost(
        authorPostId: String,
        communityId: CommunityIdDomain,
        permlink: String,
        reason: String
    )

    val feedFiltersFlow: Flow<PostFiltersHolder.FeedFilters>

    val openFeedTypeFlow: Flow<PostFiltersHolder.CurrentOpenTypeFeed>

    val userAvatarFlow: Flow<String?>
}