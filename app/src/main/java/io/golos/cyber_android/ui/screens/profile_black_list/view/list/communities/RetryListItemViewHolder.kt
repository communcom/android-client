package io.golos.cyber_android.ui.screens.profile_black_list.view.list.communities

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.BlackListListItemEventsProcessor
import kotlinx.android.synthetic.main.view_retry_list_item.view.*

class RetryListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<BlackListListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_retry_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: BlackListListItemEventsProcessor) {
        itemView.pageLoadingRetryButton.setOnClickListener { listItemEventsProcessor.retry(BlackListFilter.COMMUNITIES) }

    }

    override fun release() {
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}