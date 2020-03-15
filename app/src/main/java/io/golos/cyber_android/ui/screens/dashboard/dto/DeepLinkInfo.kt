package io.golos.cyber_android.ui.screens.dashboard.dto

import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

sealed class DeepLinkInfo {
    data class ProfileDeepLink(
        val userId: UserIdDomain
    ): DeepLinkInfo()

    data class CommunityDeepLink(
        val communityId: CommunityIdDomain
    ): DeepLinkInfo()

    data class PostDeepLink(
        val communityId: CommunityIdDomain,
        val userId: UserIdDomain,
        val postId: String
    ): DeepLinkInfo()
}