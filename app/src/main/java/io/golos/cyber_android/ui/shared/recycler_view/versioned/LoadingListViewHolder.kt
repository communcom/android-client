package io.golos.cyber_android.ui.shared.recycler_view.versioned

import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.BaseListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_progress_error.view.*

class LoadingListViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<BaseListItemEventsProcessor, LoadingListItem>(
    parentView,
    R.layout.item_progress_error
) {

    override fun init(
        listItem: LoadingListItem,
        listItemEventsProcessor: BaseListItemEventsProcessor
    ) {
        itemView.pbPageLoading.visibility = View.VISIBLE
        itemView.btnPageLoadingRetry.visibility = View.GONE
    }
}