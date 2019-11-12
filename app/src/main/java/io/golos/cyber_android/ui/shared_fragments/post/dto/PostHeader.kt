package io.golos.cyber_android.ui.shared_fragments.post.dto

import java.util.*

data class PostHeader(
    val communityName: String?,
    val communityAvatarUrl: String?,
    val actionDateTime: Date,
    val userName: String?,
    val userId: String,
    val canJoinToCommunity: Boolean,
    val canEdit: Boolean,
    val isBackFeatureEnabled: Boolean = true,
    val isJoinFeatureEnabled: Boolean = true
)