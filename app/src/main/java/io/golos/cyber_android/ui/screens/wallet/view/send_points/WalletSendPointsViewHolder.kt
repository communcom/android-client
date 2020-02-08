package io.golos.cyber_android.ui.screens.wallet.view.send_points

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.SendPointsListItem
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_wallet_send_points_list_item.view.*
import com.bumptech.glide.request.target.Target as GlideTarget

class WalletSendPointsViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletSendPointsListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_wallet_send_points_list_item
) {
    private var logoGlideTarget: GlideTarget<*>? = null

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletSendPointsListItemEventsProcessor) {
        if (listItem !is SendPointsListItem) {
            return
        }

        val context = itemView.context

        with(listItem) {
            logoGlideTarget = itemView.avatar.loadAvatar(avatarUrl)

            itemView.name.text = name

//            itemView.setOnClickListener { listItemEventsProcessor.onItemClick(listItem.community.communityId) }
        }
    }

    override fun release() {
//        itemView.setOnClickListener(null)

        logoGlideTarget?.clear(itemView.context.applicationContext)
    }
}