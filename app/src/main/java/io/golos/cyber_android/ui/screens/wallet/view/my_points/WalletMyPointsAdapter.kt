package io.golos.cyber_android.ui.screens.wallet.view.my_points

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class WalletMyPointsAdapter(
    listItemEventsProcessor: WalletMyPointsListItemEventsProcessor
) : VersionedListAdapterBase<WalletMyPointsListItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val GENERAL = 0
        const val COMMUN = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<WalletMyPointsListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            GENERAL -> WalletMyPointsViewHolder(parent)
            COMMUN -> WalletMyPointsCommunViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int = if((items[position] as MyPointsListItem).isCommun) COMMUN else GENERAL
}