package io.golos.cyber_android.ui.screens.wallet.view.history

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.wallet.dto.history.WalletHistorySeparatorListItem
import io.golos.cyber_android.ui.screens.wallet.dto.history.WalletHistoryTransferListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class WalletHistoryAdapter(
    private val listItemEventsProcessor: WalletHistoryListItemEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<WalletHistoryListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val LOADING = 0
        const val RETRY = 1
        const val SEPARATOR = 2
        const val TRANSFER = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            LOADING -> WalletHistoryLoadingViewHolder(parent)
            RETRY -> WalletHistoryRetryViewHolder(parent)
            SEPARATOR -> WalletHistorySeparatorViewHolder(parent)
            TRANSFER -> WalletHistoryTransferViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is WalletHistorySeparatorListItem -> SEPARATOR
            is WalletHistoryTransferListItem -> TRANSFER
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onHistoryNextPageReached() }
}