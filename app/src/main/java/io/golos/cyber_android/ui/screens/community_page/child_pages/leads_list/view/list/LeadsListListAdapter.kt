package io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.dto.EmptyListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.dto.LeaderListIem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.dto.RetryListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.view_holders.EmptyListItemViewHolder
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.view_holders.LeadersListItemViewHolder
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.view_holders.LoadingListItemViewHolder
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.list.view_holders.RetryListItemViewHolder

open class LeadsListListAdapter(
    listItemEventsProcessor: LeadsListItemEventsProcessor
) : VersionedListAdapterBase<LeadsListItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val LEADER = 0
        const val LOADING = 1
        const val RETRY = 2
        const val EMPTY = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<LeadsListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            LEADER -> LeadersListItemViewHolder(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            RETRY -> RetryListItemViewHolder(parent)
            EMPTY -> EmptyListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is LeaderListIem -> LEADER
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is EmptyListItem -> EMPTY
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}