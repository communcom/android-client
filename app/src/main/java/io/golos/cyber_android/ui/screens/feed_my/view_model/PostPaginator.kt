package io.golos.cyber_android.ui.screens.feed_my.view_model

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.domain.dto.ContentIdDomain
import timber.log.Timber

class PostPaginator: Paginator<Post>() {
    override fun updateItem(itemToUpdate: Post, items: List<Post>): List<Post> {
        val itemIndex = items.indexOfFirst { it.contentId == itemToUpdate.contentId }

        return if(itemIndex != -1) {
            val updatableItems = items as MutableList<Post>
            updatableItems[itemIndex] = itemToUpdate
            updatableItems
        } else {
            items
        }
    }

    override fun updateItemById(id: ContentIdDomain, items: List<Post>, updateAction: (Post) -> Post): List<Post> {
        Timber.tag("NET_SOCKET_634").d("PostPaginator::updateItemById(id: $id)")
        val itemIndex = items.indexOfFirst { it.contentId == id }
        Timber.tag("NET_SOCKET_634").d("itemIndex: $itemIndex")

        return if(itemIndex != -1) {
            val updatableItems = items as MutableList<Post>
            updatableItems[itemIndex] = updateAction(updatableItems[itemIndex])
            updatableItems
        } else {
            Timber.tag("NET_SOCKET_634").d("item not found")
            items
        }
    }
}