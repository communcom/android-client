package io.golos.cyber_android.ui.screens.community_page_post.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.RewardCurrency
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCase
import kotlinx.coroutines.flow.Flow

interface CommunityPostModel : ModelBase,
    GetPostsUseCase,
    SubscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase {

    val rewardCurrency: RewardCurrency

    val rewardCurrencyUpdates: Flow<RewardCurrency?>

    suspend fun addToFavorite(permlink: String)

    suspend fun removeFromFavorite(permlink: String)

    suspend fun deletePost(permlink: String, communityId: CommunityIdDomain)

    suspend fun upVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)

    suspend fun unVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)

    suspend fun downVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String)

    suspend fun reportPost(authorPostId: UserIdDomain, communityId: CommunityIdDomain, permlink: String, reason: String)

    suspend fun getWalletBalance(): List<WalletCommunityBalanceRecordDomain>

    suspend fun updateRewardCurrency(currency: RewardCurrency)
}