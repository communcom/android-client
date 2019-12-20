package io.golos.cyber_android.ui.screens.comment_page_menu.view.item

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentDeleteListItem
import io.golos.cyber_android.ui.utils.setDrawableToEnd
import io.golos.cyber_android.ui.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class CommentDeleteItem(
    parentView: ViewGroup
) : ViewHolderBase<CommentMenuModelListEventProcessor, CommentDeleteListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(
        listItem: CommentDeleteListItem,
        listItemEventsProcessor: CommentMenuModelListEventProcessor
    ) {
        with(itemView) {
            menuAction.text = context.getString(R.string.delete_post_menu)
            menuAction.setDrawableToEnd(R.drawable.ic_delete_red)
            menuAction.setStyle(R.style.BottomSheetMenuItem_Dangerous)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onDeleteCommentEvent()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}