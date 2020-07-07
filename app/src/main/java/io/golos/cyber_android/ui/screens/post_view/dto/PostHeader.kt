package io.golos.cyber_android.ui.screens.post_view.dto

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.RewardCurrency
import java.util.*

data class PostHeader(
    val communityName: String?,
    val communityAvatarUrl: String?,
    val communityId: CommunityIdDomain?,
    val actionDateTime: Date,
    val userName: String?,
    val userId: String,
    val userAvatarUrl: String?,
    val canJoinToCommunity: Boolean,
    val isJoinedToCommunity: Boolean = false,
    val isBackFeatureEnabled: Boolean = true,

    val reward: RewardInfo?
)