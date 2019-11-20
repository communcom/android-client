package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.post_page_menu.model.ReportListItem
import io.golos.cyber_android.utils.setDrawableToEnd
import io.golos.cyber_android.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class ReportItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, ReportListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: ReportListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.report_post)
            menuAction.setDrawableToEnd(R.drawable.ic_report)
            menuAction.setStyle(R.style.BottomSheetMenuItem_Dangerous)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onReportItemClick()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}