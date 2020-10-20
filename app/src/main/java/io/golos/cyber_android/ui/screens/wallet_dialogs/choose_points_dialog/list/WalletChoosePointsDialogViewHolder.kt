package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.glide.GlideTarget
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.load
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.format.CurrencyFormatter
import io.golos.utils.getFormattedString
import kotlinx.android.synthetic.main.dialog_wallet_items_list_general_item_view_holder.view.*


class WalletChoosePointsDialogViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletChoosePointsDialogItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.dialog_wallet_items_list_general_item_view_holder
) {
    private var logoGlideTarget: GlideTarget? = null

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletChoosePointsDialogItemEventsProcessor) {
        if (listItem !is MyPointsListItem) {
            return
        }

        val context = itemView.context

        with(listItem.data) {
            logoGlideTarget = loadIcon(itemView.logo, listItem)

            itemView.name.text = communityName?:context.resources.getString(R.string.commun_group)

            if(listItem.isCommun) {
                itemView.onHold.visibility = View.GONE
                itemView.commun.visibility = View.GONE

                itemView.points.text =
                    context.resources.getFormattedString(R.string.tokens_format, CurrencyFormatter.formatShort(context, points))

                itemView.setOnClickListener(null)
            } else {
                itemView.onHold.visibility = View.VISIBLE
                itemView.commun.visibility = View.VISIBLE

                itemView.onHold.text =
                    itemView.context.resources.getFormattedString(
                        R.string.on_hold_format,
                        CurrencyFormatter.formatShort(context, frozenPoints ?: 0.0))

                itemView.points.text =
                    context.resources.getFormattedString(R.string.points_format, CurrencyFormatter.formatShort(context, points))

                itemView.commun.text =
                    context.resources.getFormattedString(R.string.commun_format, CurrencyFormatter.formatShort(context, communs ?: 0.0))

                itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.data.communityId) }
            }
        }
    }

    override fun release() {
        itemView.setOnClickListener(null)

        logoGlideTarget?.clear(itemView.context.applicationContext)
    }

    private fun loadIcon(placeholder: ImageView, item: MyPointsListItem): GlideTarget? =
        if(item.isCommun) {
            placeholder.setImageResource(R.drawable.ic_commun)
            null
        } else {
            placeholder.load(item.data.communityLogoUrl, R.drawable.ic_commun)
        }

}