package io.golos.cyber_android.ui.screens.profile_black_list.view.list

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

class LoadingListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<BlackListListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_loading_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: BlackListListItemEventsProcessor) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}
