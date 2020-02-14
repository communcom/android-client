package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.view.WalletSendPointsAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class WalletChooseFriendDialogAdapter(
    private val listItemEventsProcessor: WalletChooseFriendDialogItemEventsProcessor,
    pageSize: Int?
) : WalletSendPointsAdapterBase<WalletChooseFriendDialogItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletChooseFriendDialogItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            SEND_POINTS -> WalletChooseFriendDialogGeneralViewHolder(parent)
            LOADING -> WalletChooseFriendDialogLoadingViewHolder(parent)
            RETRY -> WalletChooseFriendDialogRetryViewHolder(parent)
            NO_DATA -> WalletChooseFriendDialogNoDataViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onNextPageReached() }
}