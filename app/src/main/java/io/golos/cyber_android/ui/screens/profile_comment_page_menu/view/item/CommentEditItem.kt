package io.golos.cyber_android.ui.screens.profile_comment_page_menu.view.item

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.profile_comment_page_menu.model.CommentMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.profile_comment_page_menu.model.item.CommentEditListItem
import io.golos.cyber_android.ui.utils.setDrawableToEnd
import io.golos.cyber_android.ui.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class CommentEditItem(
    parentView: ViewGroup
) : ViewHolderBase<CommentMenuModelListEventProcessor, CommentEditListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(
        listItem: CommentEditListItem,
        listItemEventsProcessor: CommentMenuModelListEventProcessor
    ) {
        with(itemView) {
            menuAction.text = context.getString(R.string.edit_post_menu)
            menuAction.setDrawableToEnd(R.drawable.ic_edit)
            menuAction.setStyle(R.style.BottomSheetMenuItem)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onEditCommentEvent()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}