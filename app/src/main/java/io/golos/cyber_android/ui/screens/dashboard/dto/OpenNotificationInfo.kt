package io.golos.cyber_android.ui.screens.dashboard.dto

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

sealed class OpenNotificationInfo {
    data class OpenPost(
        val contentId: ContentId
    ): OpenNotificationInfo()

    data class OpenProfile(
        val userId: UserIdDomain
    ): OpenNotificationInfo()

    data class OpenWallet(
        val balance: List<WalletCommunityBalanceRecordDomain>
    ): OpenNotificationInfo()
}