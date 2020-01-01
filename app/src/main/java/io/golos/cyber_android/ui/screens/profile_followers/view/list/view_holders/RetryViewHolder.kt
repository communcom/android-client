package io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.RetryListItem
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import kotlinx.android.synthetic.main.view_retry_list_item.view.*

class RetryViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<FollowersListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_retry_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: FollowersListItemEventsProcessor) {
        itemView.pageLoadingRetryButton.setOnClickListener {
            listItemEventsProcessor.retry((listItem as RetryListItem).filter)
        }
   }

    override fun release() {
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}