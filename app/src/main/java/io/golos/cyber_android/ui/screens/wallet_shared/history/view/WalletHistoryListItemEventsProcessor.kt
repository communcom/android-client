package io.golos.cyber_android.ui.screens.wallet_shared.history.view

interface WalletHistoryListItemEventsProcessor {
    fun onHistoryNextPageReached()

    fun onHistoryRetryClick()

    fun onFilterClick()
}