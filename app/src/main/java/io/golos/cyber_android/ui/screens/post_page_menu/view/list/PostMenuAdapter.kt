package io.golos.cyber_android.ui.screens.post_page_menu.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.post_page_menu.model.*
import io.golos.cyber_android.ui.screens.post_page_menu.view.items.*

class PostMenuAdapter(
    processor: PostMenuModelListEventProcessor
) : VersionedListAdapterBase<PostMenuModelListEventProcessor>(processor, 0) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem> {
        return when (viewType) {
            PostMenuViewType.ADD_FAVORITE_ITEM -> {
                AddToFavoriteItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.REMOVE_FAVORITE_ITEM -> {
                RemoveFromFavoriteItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.SHARE_ITEM -> {
                ShareItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.EDIT_ITEM -> {
                EditItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.DELETE_ITEM -> {
                DeleteItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.JOIN_ITEM -> {
                JoinItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.JOINED_ITEM -> {
                JoinedItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            PostMenuViewType.REPORT_ITEM -> {
                ReportItem(parent) as ViewHolderBase<PostMenuModelListEventProcessor, VersionedListItem>
            }
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is FavoriteListItem -> PostMenuViewType.ADD_FAVORITE_ITEM
        is RemoveFavoriteListItem -> PostMenuViewType.REMOVE_FAVORITE_ITEM
        is ShareListItem -> PostMenuViewType.SHARE_ITEM
        is EditListItem -> PostMenuViewType.EDIT_ITEM
        is DeleteListItem -> PostMenuViewType.DELETE_ITEM
        is JoinListItem -> PostMenuViewType.JOIN_ITEM
        is JoinedListItem -> PostMenuViewType.JOINED_ITEM
        is ReportListItem -> PostMenuViewType.REPORT_ITEM
        else -> throw UnsupportedOperationException("This type of item is not supported")
    }
}