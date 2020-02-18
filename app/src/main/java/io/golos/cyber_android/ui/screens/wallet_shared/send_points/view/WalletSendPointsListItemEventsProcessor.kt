package io.golos.cyber_android.ui.screens.wallet_shared.send_points.view

import io.golos.domain.dto.UserDomain

interface WalletSendPointsListItemEventsProcessor {
    fun onSendPointsItemClick(user: UserDomain?)

    fun onSendPointsNextPageReached()

    fun onSendPointsRetryClick()
}