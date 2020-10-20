package io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

@BindingAdapter("wallet_send_points_page_size")
fun setWalletSendPointsViewPageSizeBinding(view: WalletSendPointsView, pageSize: Int) {
    view.setPageSize(pageSize)
}

@BindingAdapter("wallet_send_points_events_processor")
fun setWalletSendPointsViewEventsProcessorBinding(view: WalletSendPointsView, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
    view.setEventsProcessor(listItemEventsProcessor)
}

@BindingAdapter("wallet_send_points_items")
fun setWalletSendPointsViewItemsBinding(view: WalletSendPointsView, items: LiveData<List<VersionedListItem>>?) =
    items?.value?.let { view.setItems(it) }
