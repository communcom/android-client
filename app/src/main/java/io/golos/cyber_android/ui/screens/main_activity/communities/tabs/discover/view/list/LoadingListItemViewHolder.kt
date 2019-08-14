package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.LoadingListItem

class LoadingListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, ListItem>(
    LayoutInflater.from(parentView.context).inflate(R.layout.view_communities_loading_list_item, parentView, false)
) {
    override fun init(listItem: ListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}
