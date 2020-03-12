package io.golos.cyber_android.ui.screens.wallet_shared.history.view

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

@BindingAdapter("wallet_history_page_size")
fun setWalletHistoryViewPageSizeBinding(view: WalletHistoryView, pageSize: Int) {
    view.setPageSize(pageSize)
}

@BindingAdapter("wallet_history_events_processor")
fun setWalletHistoryViewEventsProcessorBinding(view: WalletHistoryView, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
    view.setEventsProcessor(listItemEventsProcessor)
}

@BindingAdapter("wallet_history_items")
fun setWalletHistoryViewItemsBinding(view: WalletHistoryView, items: LiveData<List<VersionedListItem>>?) {
    items?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setItems(it) })
        }
    }
}
