package io.golos.cyber_android.ui.dialogs.select_community_dialog.view

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListAdapter
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders.LoadingListItemViewHolder
import io.golos.cyber_android.ui.screens.communities_list.view.list.view_holders.RetryListItemViewHolder

class CommunityListAdapterDialog(
    private val listItemEventsProcessor: CommunityListItemEventsProcessor,
    pageSize: Int?
) : CommunityListAdapter(listItemEventsProcessor, pageSize) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            COMMUNITY -> CommunityListItemViewHolderDialog(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            RETRY -> RetryListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}