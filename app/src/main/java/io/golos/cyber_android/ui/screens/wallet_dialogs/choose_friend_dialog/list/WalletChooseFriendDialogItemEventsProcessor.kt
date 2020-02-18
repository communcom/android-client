package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list

import io.golos.domain.dto.UserDomain

interface WalletChooseFriendDialogItemEventsProcessor {
    fun onNextPageReached()

    fun onRetryClick()

    fun onItemClick(user: UserDomain)
}