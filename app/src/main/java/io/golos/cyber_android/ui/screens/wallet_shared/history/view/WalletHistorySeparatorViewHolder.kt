package io.golos.cyber_android.ui.screens.wallet_shared.history.view

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistorySeparatorListItem
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistorySeparatorType
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import kotlinx.android.synthetic.main.view_wallet_history_separator_list_item.view.*

class WalletHistorySeparatorViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_history_separator_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        if (listItem !is WalletHistorySeparatorListItem) {
            return
        }

        with(parentView.context.resources) {
            itemView.dateLabel.text = when(listItem.type) {
                WalletHistorySeparatorType.TODAY -> getText(R.string.today)
                WalletHistorySeparatorType.YESTERDAY -> getText(R.string.yesterday)
                WalletHistorySeparatorType.DAYS_AGO -> getFormattedString(R.string.days_ago, listItem.days!!)
                WalletHistorySeparatorType.MONTH_AGO -> getString(R.string.month_ago)
                WalletHistorySeparatorType.PREVIOUSLY -> getString(R.string.previously)
            }
        }
    }

    override fun release() {
//        itemView.setOnClickListener(null)
    }
}