package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list

import io.golos.cyber_android.ui.common.recycler_view.DiffAlgBase
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.dto.LoadingListItem

class CommunityListItemDiffAlg(
    oldList: List<ListItem>,
    newList: List<ListItem>
) : DiffAlgBase<ListItem>(oldList, newList) {

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is CommunityListItem && newItem is CommunityListItem -> {
                oldItem.externalId == newItem.externalId
            }
            oldItem is LoadingListItem && newItem is LoadingListItem -> true
            else -> false
        }
    }
}