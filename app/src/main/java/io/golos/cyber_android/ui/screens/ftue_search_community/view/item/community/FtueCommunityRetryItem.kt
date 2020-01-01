package io.golos.cyber_android.ui.screens.ftue_search_community.view.item.community

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.community.FtueCommunityRetryListItem
import kotlinx.android.synthetic.main.item_progress_error.view.*

class FtueCommunityRetryItem(
    parentView: ViewGroup
) : ViewHolderBase<FtueItemListModelEventProcessor, FtueCommunityRetryListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(listItem: FtueCommunityRetryListItem, listItemEventsProcessor: FtueItemListModelEventProcessor) {
        itemView.pbPageLoading.visibility = View.GONE
        itemView.btnPageLoadingRetry.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.setOnClickListener {
            listItemEventsProcessor.onRetryLoadCommunity()
        }
    }
}