package io.golos.cyber_android.ui.screens.wallet.view.my_points

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem

@BindingAdapter("wallet_my_points_events_processor")
fun setWalletMyPointsViewEventsProcessorBinding(view: WalletMyPointsView, listItemEventsProcessor: WalletMyPointsListItemEventsProcessor) {
    view.setEventsProcessor(listItemEventsProcessor)
}

@BindingAdapter("wallet_my_points_items")
fun setWalletMyPointsViewItemsBinding(view: WalletMyPointsView, items: LiveData<List<MyPointsListItem>>?) =
    items?.value?.let { view.setItems(it) }
