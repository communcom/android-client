package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.common.recycler_view.ListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.LoadingListItem

class CommunityListAdapter(
    listItemEventsProcessor: CommunityListItemEventsProcessor
) : ListAdapterBase<CommunityListItemEventsProcessor, ListItem>(listItemEventsProcessor) {

    private companion object {
        const val COMMUNITY = 0
        const val LOADING = 1
    }

    override fun createDiffAlg(oldData: List<ListItem>, newData: List<ListItem>): DiffAlgBase<ListItem> =
        CommunityListItemDiffAlg(oldData, newData)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<CommunityListItemEventsProcessor, ListItem> =
        when(viewType) {
            COMMUNITY -> CommunityListItemViewHolder(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is CommunityListItem -> COMMUNITY
            is LoadingListItem -> LOADING
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}