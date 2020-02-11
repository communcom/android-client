package io.golos.cyber_android.ui.screens.wallet.view.history

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_history_retry_list_item.view.*

class WalletHistoryRetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem>(
    parentView,

    R.layout.view_wallet_history_retry_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        itemView.btnRetry.setOnClickListener { listItemEventsProcessor.onHistoryRetryClick() }
    }

    override fun release() {
        itemView.btnRetry.setOnClickListener(null)
    }
}