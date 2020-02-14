package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_points_dialog.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class WalletChoosePointsDialogAdapter(
    listItemEventsProcessor: WalletChoosePointsDialogItemEventsProcessor
) : VersionedListAdapterBase<WalletChoosePointsDialogItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val GENERAL = 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<WalletChoosePointsDialogItemEventsProcessor, VersionedListItem> =

        when(viewType) {
            GENERAL -> WalletChoosePointsDialogViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =  GENERAL
}