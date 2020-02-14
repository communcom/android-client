package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list

import io.golos.domain.dto.UserIdDomain

interface WalletChooseFriendDialogItemEventsProcessor {
    fun onNextPageReached()

    fun onRetryClick()

    fun onItemClick(userId: UserIdDomain)
}