package io.golos.cyber_android.ui.screens.wallet_shared.send_points.view

import android.view.ViewGroup
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class WalletSendPointsAdapter(
    private val listItemEventsProcessor: WalletSendPointsListItemEventsProcessor,
    pageSize: Int?
) : WalletSendPointsAdapterBase<WalletSendPointsListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            SEND_POINTS -> WalletSendPointsViewHolder(parent)
            LOADING -> WalletSendPointsLoadingViewHolder(parent)
            RETRY -> WalletSendPointsRetryViewHolder(parent)
            NO_DATA -> WalletSendPointsNoDataViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onSendPointsNextPageReached() }
}