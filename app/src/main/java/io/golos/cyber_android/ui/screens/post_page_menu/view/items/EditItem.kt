package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.EditListItem
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.utils.setDrawableToEnd
import io.golos.cyber_android.ui.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class EditItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, EditListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: EditListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.edit_post_menu)
            menuAction.setDrawableToEnd(R.drawable.ic_edit)
            menuAction.setStyle(R.style.BottomSheetMenuItem)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onEditItemClick()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}