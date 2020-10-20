package io.golos.cyber_android.ui.screens.wallet_shared.history.view

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistorySeparatorListItem
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistoryTransferListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.*

open class WalletHistoryAdapter(
    private val listItemEventsProcessor: WalletHistoryListItemEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<WalletHistoryListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val LOADING = 0
        const val RETRY = 1
        const val NO_DATA = 2
        const val SEPARATOR = 3
        const val TRANSFER = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            LOADING -> WalletHistoryLoadingViewHolder(
                parent
            )
            RETRY -> WalletHistoryRetryViewHolder(
                parent
            )
            NO_DATA -> WalletHistoryNoDataViewHolder(
                parent
            )
            SEPARATOR -> WalletHistorySeparatorViewHolder(
                parent
            )
            TRANSFER -> WalletHistoryTransferViewHolder(
                parent
            )
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is NoDataListItem -> NO_DATA
            is WalletHistorySeparatorListItem -> SEPARATOR
            is WalletHistoryTransferListItem -> TRANSFER
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onHistoryNextPageReached() }
}