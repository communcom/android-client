package io.golos.cyber_android.ui.screens.wallet_shared.send_points.view

import io.golos.domain.dto.UserIdDomain

interface WalletSendPointsListItemEventsProcessor {
    fun onSendPointsItemClick(userId: UserIdDomain)

    fun onSendPointsNextPageReached()

    fun onSendPointsRetryClick()
}