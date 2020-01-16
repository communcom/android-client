package io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_members.view.UsersListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class LoadingViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<UsersListEventsProcessor, VersionedListItem>(parentView, R.layout.view_loading_list_item) {

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: UsersListEventsProcessor) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}
