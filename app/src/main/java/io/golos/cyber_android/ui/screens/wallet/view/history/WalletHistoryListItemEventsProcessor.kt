package io.golos.cyber_android.ui.screens.wallet.view.history

interface WalletHistoryListItemEventsProcessor {
    fun onHistoryNextPageReached()

    fun onHistoryRetryClick()
}