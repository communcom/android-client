package io.golos.cyber_android.ui.screens.post_page_menu.view.items

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.screens.post_page_menu.model.FavoriteListItem
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenuModelListEventProcessor
import io.golos.cyber_android.ui.utils.setDrawableToEnd
import io.golos.cyber_android.ui.utils.setStyle
import kotlinx.android.synthetic.main.item_post_menu.view.*

class AddToFavoriteItem(
    parentView: ViewGroup
) : ViewHolderBase<PostMenuModelListEventProcessor, FavoriteListItem>(
    parentView,
    R.layout.item_post_menu
) {

    override fun init(listItem: FavoriteListItem, listItemEventsProcessor: PostMenuModelListEventProcessor) {
        with(itemView) {
            menuAction.text = context.getString(R.string.favorite_add_post)
            menuAction.setDrawableToEnd(R.drawable.ic_favorite)
            menuAction.setStyle(R.style.BottomSheetMenuItem)
            menuAction.setOnClickListener {
                listItemEventsProcessor.onAddToFavoriteItemClick()
            }
        }
    }

    override fun release() {
        itemView.menuAction.setOnClickListener(null)
    }
}