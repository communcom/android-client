package io.golos.cyber_android.ui.screens.wallet.view.history

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_no_data_stub.view.*

class WalletHistoryNoDataViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_no_data_stub
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        itemView.noDataTitle.text = parentView.context.resources.getText(R.string.no_history)
        itemView.noDataExplanation.text = parentView.context.resources.getText(R.string.no_history_explanation)
    }
}