package io.golos.cyber_android.ui.screens.wallet.view.my_points

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("wallet_my_points_events_processor")
fun setWalletMyPointsViewEventsProcessorBinding(view: WalletMyPointsView, listItemEventsProcessor: WalletMyPointsListItemEventsProcessor) {
    view.setEventsProcessor(listItemEventsProcessor)
}

@BindingAdapter("wallet_my_points_items")
fun setWalletMyPointsViewItemsBinding(view: WalletMyPointsView, items: LiveData<List<MyPointsListItem>>?) {
    items?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setItems(it) })
        }
    }
}
