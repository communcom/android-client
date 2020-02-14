package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.dialog_wallet_items_list_general_item_view_holder.view.*
import com.bumptech.glide.request.target.Target as GlideTarget

class WalletChooseFriendDialogGeneralViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletChooseFriendDialogItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.dialog_wallet_items_list_general_item_view_holder
) {
    private var logoGlideTarget: GlideTarget<*>? = null

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletChooseFriendDialogItemEventsProcessor) {
        if (listItem !is SendPointsListItem) {
            return
        }

        with(listItem) {
            logoGlideTarget = itemView.logo.loadAvatar(avatarUrl)

            itemView.name.text = name

            itemView.points.visibility = View.GONE
            itemView.onHold.visibility = View.GONE
            itemView.commun.visibility = View.GONE

            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.userId) }
        }
    }

    override fun release() {
        itemView.setOnClickListener(null)

        logoGlideTarget?.clear(itemView.context.applicationContext)
    }
}