package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.DeleteListItem
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.utils.setDrawableToEnd
import io.golos.cyber_android.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class DeleteItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, DeleteListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: DeleteListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.delete_post_menu)
            menuAction.setDrawableToEnd(R.drawable.ic_delete_red)
            menuAction.setStyle(R.style.BottomSheetMenuItem_Dangerous)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onDeleteItemClick()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}