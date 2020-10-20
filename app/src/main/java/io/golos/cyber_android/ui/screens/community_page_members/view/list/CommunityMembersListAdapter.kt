package io.golos.cyber_android.ui.screens.community_page_members.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.community_page_members.dto.CommunityUserListItem
import io.golos.cyber_android.ui.screens.community_page_members.view.MembersListEventsProcessor
import io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders.CommunityUserViewHolder
import io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders.LoadingViewHolder
import io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders.RetryViewHolder
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class CommunityMembersListAdapter(
    private val listItemEventsProcessor: MembersListEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<MembersListEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val USER = 0
        const val LOADING = 1
        const val RETRY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<MembersListEventsProcessor, VersionedListItem> =
        when(viewType) {
            USER -> CommunityUserViewHolder(parent)
            LOADING -> LoadingViewHolder(parent)
            RETRY -> RetryViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is CommunityUserListItem -> USER
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onNextPageReached() }
}