package io.golos.cyber_android.ui.screens.wallet.view.history

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class WalletHistoryLoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem>(
    parentView,

    R.layout.view_wallet_history_loading_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        // Do nothing
    }
}