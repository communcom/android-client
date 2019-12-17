package io.golos.cyber_android.ui.screens.comment_page_menu.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.model.CommentMenuModelListEventProcessor
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentDeleteListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.model.item.CommentEditListItem
import io.golos.cyber_android.ui.screens.comment_page_menu.view.item.CommentDeleteItem
import io.golos.cyber_android.ui.screens.comment_page_menu.view.item.CommentEditItem
import io.golos.cyber_android.ui.screens.comment_page_menu.view.list.CommentMenuViewType.COMMENT_MENU_DELETE_TYPE
import io.golos.cyber_android.ui.screens.comment_page_menu.view.list.CommentMenuViewType.COMMENT_MENU_EDIT_TYPE

class CommentMenuAdapter(
    processor: CommentMenuModelListEventProcessor
) : VersionedListAdapterBase<CommentMenuModelListEventProcessor>(processor, null) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<CommentMenuModelListEventProcessor, VersionedListItem> {
        return when (viewType) {
            COMMENT_MENU_EDIT_TYPE -> {
                CommentEditItem(parent) as ViewHolderBase<CommentMenuModelListEventProcessor, VersionedListItem>
            }
            COMMENT_MENU_DELETE_TYPE -> {
                CommentDeleteItem(parent) as ViewHolderBase<CommentMenuModelListEventProcessor, VersionedListItem>
            }
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int  = when (items[position]) {
        is CommentEditListItem -> COMMENT_MENU_EDIT_TYPE
        is CommentDeleteListItem -> COMMENT_MENU_DELETE_TYPE
        else -> throw UnsupportedOperationException("This type of item is not supported")
    }
}