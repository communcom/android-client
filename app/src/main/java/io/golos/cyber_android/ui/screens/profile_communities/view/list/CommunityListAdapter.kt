package io.golos.cyber_android.ui.screens.profile_communities.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunityListItem

open class CommunityListAdapter(
    listItemEventsProcessor: CommunityListItemEventsProcessor
) : VersionedListAdapterBase<CommunityListItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val COMMUNITY = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            COMMUNITY -> CommunityListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is CommunityListItem -> COMMUNITY
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}