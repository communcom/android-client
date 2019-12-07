package io.golos.cyber_android.ui.screens.ftue_search_community.view.item

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueItemListModelEventProcessor
import io.golos.cyber_android.ui.screens.ftue_search_community.model.item.FtueCommunityProgressListItem
import kotlinx.android.synthetic.main.item_progress_error.view.*

class FtueCommunityProgressItem(
    parentView: ViewGroup
) : ViewHolderBase<FtueItemListModelEventProcessor, FtueCommunityProgressListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(listItem: FtueCommunityProgressListItem, listItemEventsProcessor: FtueItemListModelEventProcessor) {
        itemView.pbPageLoading.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.visibility = View.GONE
    }
}