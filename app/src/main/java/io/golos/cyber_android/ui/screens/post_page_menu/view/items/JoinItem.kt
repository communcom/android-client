package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.post_page_menu.model.JoinListItem
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.utils.setDrawableToEnd
import io.golos.cyber_android.ui.shared.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class JoinItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, JoinListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: JoinListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.follow)
            menuAction.setDrawableToEnd(R.drawable.ic_join)
            menuAction.setStyle(R.style.BottomSheetMenuItem)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onJoinItemClick()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}