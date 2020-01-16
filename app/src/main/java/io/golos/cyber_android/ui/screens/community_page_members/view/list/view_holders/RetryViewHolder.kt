package io.golos.cyber_android.ui.screens.community_page_members.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_members.view.UsersListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_retry_list_item.view.*

class RetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<UsersListEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_retry_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: UsersListEventsProcessor) {
        itemView.pageLoadingRetryButton.setOnClickListener {
            listItemEventsProcessor.retry()
        }
    }

    override fun release() {
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}