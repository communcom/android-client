package io.golos.cyber_android.ui.screens.wallet_shared.history.view

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistoryConstants
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistoryTransferDirection
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistoryTransferListItem
import io.golos.cyber_android.ui.screens.wallet_shared.history.dto.WalletHistoryTransferType
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.cyber_android.ui.shared.formatters.currency.CurrencyFormatter
import io.golos.cyber_android.ui.shared.glide.GlideTarget
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.load
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_history_transfer_list_item.view.*

class WalletHistoryTransferViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletHistoryListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_history_transfer_list_item
) {
    private var smallIconGlideTarget: GlideTarget? = null
    private var mainIconGlideTarget: GlideTarget? = null


    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
        if (listItem !is WalletHistoryTransferListItem) {
            return
        }

        val context = parentView.context
        val resources = context.resources

        itemView.name.text = listItem.displayName

        itemView.operation.visibility = if(listItem.type == WalletHistoryTransferType.NONE) View.GONE else View.VISIBLE
        itemView.operation.text = when(listItem.type) {
            WalletHistoryTransferType.REWARD -> resources.getString(R.string.reward)
            WalletHistoryTransferType.CONVERT -> resources.getString(R.string.convert)
            WalletHistoryTransferType.TRANSFER -> resources.getString(R.string.transfer)
            WalletHistoryTransferType.NONE -> ""
        }

        itemView.dateTime.text = if(listItem.isOnHold) resources.getText(R.string.on_hold) else listItem.timeStamp

        val amountColorRes = if(listItem.direction == WalletHistoryTransferDirection.SEND) R.color.black else R.color.green_bright
        itemView.amount.setTextColor(resources.getColorRes(amountColorRes))

        val amountSign = if(listItem.isOnHold) {
            ""
        } else {
            when(listItem.direction) {
                WalletHistoryTransferDirection.SEND -> "-"
                WalletHistoryTransferDirection.RECEIVE -> "+"
            }
        }
        itemView.amount.text = "$amountSign ${CurrencyFormatter.formatShort(context, listItem.coinsQuantity)} ${listItem.coinsSymbol}"

        itemView.smallIcon.visibility = if(listItem.isSmallIconVisible) View.VISIBLE else View.GONE

        smallIconGlideTarget = loadIcon(itemView.smallIcon, listItem.smallIcon)
        mainIconGlideTarget = loadIcon(itemView.mainIcon, listItem.mainIcon)
    }

    override fun release() {
        smallIconGlideTarget?.clear(itemView.context.applicationContext)
        mainIconGlideTarget?.clear(itemView.context.applicationContext)
    }

    private fun loadIcon(placeholder: ImageView, icon: String?): GlideTarget? =
        if(icon.isNullOrEmpty() || icon == WalletHistoryConstants.ICON_COMMUN) {
            placeholder.setImageResource(R.drawable.ic_commun)
            null
        } else {
            if(icon == WalletHistoryConstants.ICON_LIKE) {
                placeholder.setImageResource(R.drawable.ic_wallet_like)
                null
            } else {
                placeholder.load(icon, R.drawable.ic_commun)
            }
        }
}