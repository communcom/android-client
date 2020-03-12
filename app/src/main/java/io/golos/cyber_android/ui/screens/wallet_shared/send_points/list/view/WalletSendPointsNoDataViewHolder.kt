package io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_no_data_stub.view.*

class WalletSendPointsNoDataViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_no_data_stub
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        itemView.noDataTitle.text = parentView.context.resources.getText(R.string.no_followings)
        itemView.noDataExplanation.text = parentView.context.resources.getText(R.string.no_followings_explanation)
    }
}