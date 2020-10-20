package io.golos.cyber_android.ui.shared.recycler_view.versioned

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.SupportRetryListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_progress_error.view.*

class RetryListViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<SupportRetryListItemEventsProcessor, RetryListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(
        listItem: RetryListItem,
        listItemEventsProcessor: SupportRetryListItemEventsProcessor
    ) {
        itemView.pbPageLoading.visibility = View.GONE
        itemView.btnPageLoadingRetry.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.setOnClickListener {
            listItemEventsProcessor.onRetryLoadPage()
        }
    }
}