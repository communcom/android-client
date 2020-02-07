package io.golos.cyber_android.ui.screens.wallet.view.my_points

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadCommunityItemAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import kotlinx.android.synthetic.main.view_wallet_my_points_list_item.view.*
import com.bumptech.glide.request.target.Target as GlideTarget

class WalletMyPointsViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletMyPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_my_points_list_item
) {
    private var logoGlideTarget: GlideTarget<*>? = null

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletMyPointsListItemEventsProcessor) {
        if (listItem !is MyPointsListItem) {
            return
        }

        val context = itemView.context

        with(listItem.data) {
            logoGlideTarget = itemView.logo.loadCommunityItemAvatar(communityLogoUrl)

            itemView.title.text = communityName

            itemView.onHoldLabel.text =
                itemView.context.resources.getFormattedString(
                    R.string.on_hold_format,
                    CurrencyFormatter.formatShort(context, frozenPoints ?: 0.0))

            itemView.pointsValue.text = CurrencyFormatter.formatShort(context, points)

            itemView.communsLabel.text =
                context.resources.getFormattedString(R.string.commun_format, CurrencyFormatter.formatShort(context, communs ?: 0.0))

//            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community.communityId) }
        }
    }

    override fun release() {
//        itemView.setOnClickListener(null)

        logoGlideTarget?.clear(itemView.context.applicationContext)
    }
}