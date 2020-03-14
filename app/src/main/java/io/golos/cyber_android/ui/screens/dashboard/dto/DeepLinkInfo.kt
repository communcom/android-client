package io.golos.cyber_android.ui.screens.dashboard.dto

import io.golos.domain.dto.UserIdDomain

sealed class DeepLinkInfo {
    data class ProfileDeepLink(
        val userId: UserIdDomain
    ): DeepLinkInfo()

    data class CommunityDeepLink(
        val communityAlias: String
    ): DeepLinkInfo()

    data class PostDeepLink(
        val communityAlias: String,
        val userName: String,
        val postId: String
    ): DeepLinkInfo()
}