package io.golos.cyber_android.ui.screens.main_activity.communities.view.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase

class LoadingListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, ListItem>(
    parentView,
    R.layout.view_communities_loading_list_item
) {
    override fun init(listItem: ListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}
