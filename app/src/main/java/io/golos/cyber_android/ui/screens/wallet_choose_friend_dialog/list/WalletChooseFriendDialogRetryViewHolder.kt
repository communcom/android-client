package io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.dialog_wallet_items_list_retry_item_view_holder.view.*

class WalletChooseFriendDialogRetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<WalletChooseFriendDialogItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.dialog_wallet_items_list_retry_item_view_holder
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletChooseFriendDialogItemEventsProcessor) {
        itemView.btnRetry.setOnClickListener { listItemEventsProcessor.onRetryClick() }
    }

    override fun release() {
        itemView.btnRetry.setOnClickListener(null)
    }
}