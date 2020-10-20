package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.domain.dto.CommunityIdDomain

data class Community(
    val communityId: CommunityIdDomain,
    val name: String,
    val logo: String?,
    val followersCount: Int,
    var isSubscribed: Boolean
)