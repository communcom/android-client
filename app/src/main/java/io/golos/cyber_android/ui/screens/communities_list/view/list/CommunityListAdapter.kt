package io.golos.cyber_android.ui.screens.communities_list.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders.CommunityListItemViewHolder
import io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders.LoadingListItemViewHolder
import io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders.RetryListItemViewHolder
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.*

open class CommunityListAdapter(
    private val listItemEventsProcessor: CommunityListItemEventsProcessor,
    pageSize: Int?
) : VersionedListAdapterBase<CommunityListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val COMMUNITY = 0
        const val LOADING = 1
        const val RETRY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            COMMUNITY -> CommunityListItemViewHolder(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            RETRY -> RetryListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is CommunityListItem -> COMMUNITY
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() = listItemEventsProcessor.onNextPageReached()
}