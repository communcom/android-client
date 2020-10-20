package io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view

import io.golos.domain.dto.UserBriefDomain

interface WalletSendPointsListItemEventsProcessor {
    fun onSendPointsItemClick(user: UserBriefDomain?)

    fun onSendPointsNextPageReached()

    fun onSendPointsRetryClick()
}