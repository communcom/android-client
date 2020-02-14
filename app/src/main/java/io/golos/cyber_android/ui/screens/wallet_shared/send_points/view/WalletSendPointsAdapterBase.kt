package io.golos.cyber_android.ui.screens.wallet_shared.send_points.view

import io.golos.cyber_android.ui.screens.wallet_shared.send_points.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.NoDataListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase

abstract class WalletSendPointsAdapterBase<TListItemEventsProcessor>(
    listItemEventsProcessor: TListItemEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<TListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val SEND_POINTS = 0
        const val LOADING = 1
        const val RETRY = 2
        const val NO_DATA = 3
    }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is SendPointsListItem -> SEND_POINTS
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is NoDataListItem -> NO_DATA
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}