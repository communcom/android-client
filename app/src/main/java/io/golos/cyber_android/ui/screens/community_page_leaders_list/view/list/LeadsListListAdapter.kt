package io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.*
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.list.view_holders.*
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class LeadsListListAdapter(
    listItemEventsProcessor: LeadsListItemEventsProcessor
) : VersionedListAdapterBase<LeadsListItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val LEADER = 0
        const val LOADING = 1
        const val RETRY = 2
        const val EMPTY = 3
        const val LEADER_HEADER = 4
        const val NOMINEES_HEADER = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<LeadsListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            LEADER -> LeadersListItemViewHolder(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            RETRY -> RetryListItemViewHolder(parent)
            EMPTY -> EmptyListItemViewHolder(parent)
            LEADER_HEADER -> LeadersHeaderListItemViewHolder(parent)
            NOMINEES_HEADER -> NomineesHeaderListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is LeaderListItem -> LEADER
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is EmptyListItem -> EMPTY
            is LeadersHeaderItem -> LEADER_HEADER
            is NomineesHeaderItem -> NOMINEES_HEADER
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}