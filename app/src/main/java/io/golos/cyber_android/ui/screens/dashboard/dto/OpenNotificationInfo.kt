package io.golos.cyber_android.ui.screens.dashboard.dto

import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

sealed class OpenNotificationInfo {
    data class OpenPost(
        val contentId: ContentIdDomain
    ): OpenNotificationInfo()

    data class OpenProfile(
        val userId: UserIdDomain
    ): OpenNotificationInfo()

    data class OpenWallet(
        val balance: List<WalletCommunityBalanceRecordDomain>
    ): OpenNotificationInfo()
}