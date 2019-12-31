package io.golos.cyber_android.ui.screens.post_view.dto

import java.util.*

data class PostHeader(
    val communityName: String?,
    val communityAvatarUrl: String?,
    val actionDateTime: Date,
    val userName: String?,
    val userId: String,
    val canJoinToCommunity: Boolean,
    val isJoinedToCommunity: Boolean = true,
    val isBackFeatureEnabled: Boolean = true,
    val isJoinFeatureEnabled: Boolean = true
)