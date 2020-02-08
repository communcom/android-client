package io.golos.cyber_android.ui.screens.wallet.view.send_points

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class WalletSendPointsLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_send_points_list_item_loading
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        // Do nothing
    }
}