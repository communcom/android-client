package io.golos.cyber_android.ui.screens.wallet.view.send_points

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.wallet.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.*

open class WalletSendPointsAdapter(
    private val listItemEventsProcessor: WalletSendPointsListItemEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<WalletSendPointsListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val SEND_POINTS = 0
        const val LOADING = 1
        const val RETRY = 2
        const val NO_DATA = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            SEND_POINTS -> WalletSendPointsViewHolder(parent)
            LOADING -> WalletSendPointsLoadingViewHolder(parent)
            RETRY -> WalletSendPointsRetryViewHolder(parent)
            NO_DATA -> WalletSendPointsNoDataViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is SendPointsListItem -> SEND_POINTS
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is NoDataListItem -> NO_DATA
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onSendPointsNextPageReached() }
}