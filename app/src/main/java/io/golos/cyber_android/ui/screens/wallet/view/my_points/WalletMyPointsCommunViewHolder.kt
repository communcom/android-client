package io.golos.cyber_android.ui.screens.wallet.view.my_points

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_my_points_list_item_commun.view.*

class WalletMyPointsCommunViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletMyPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_my_points_list_item_commun
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletMyPointsListItemEventsProcessor) {
        if (listItem !is MyPointsListItem) {
            return
        }

        val context = parentView.context

        with(listItem.data) {
            itemView.pointsValue.text = CurrencyFormatter.formatShort(context, points)

//            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community.communityId) }
        }
    }

    override fun release() {
//        itemView.setOnClickListener(null)
    }
}