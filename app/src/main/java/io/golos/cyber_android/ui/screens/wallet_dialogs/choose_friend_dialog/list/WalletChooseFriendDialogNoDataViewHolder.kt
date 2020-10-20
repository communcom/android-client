package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_no_data_stub.view.*

class WalletChooseFriendDialogNoDataViewHolder(
    private val parentView: ViewGroup
) : ViewHolderBase<WalletChooseFriendDialogItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.dialog_wallet_items_list_no_data_item_view_holder
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: WalletChooseFriendDialogItemEventsProcessor) {
        itemView.noDataTitle.text = parentView.context.resources.getText(R.string.no_followings)
        itemView.noDataExplanation.text = parentView.context.resources.getText(R.string.no_followings_explanation)
    }
}