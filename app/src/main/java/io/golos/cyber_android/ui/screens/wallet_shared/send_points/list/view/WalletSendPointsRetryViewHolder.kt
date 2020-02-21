package io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_send_points_list_item_retry.view.*

class WalletSendPointsRetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_send_points_list_item_retry
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        itemView.btnRetry.setOnClickListener { listItemEventsProcessor.onSendPointsRetryClick() }
    }

    override fun release() {
        itemView.btnRetry.setOnClickListener(null)
    }
}