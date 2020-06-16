package io.golos.cyber_android.ui.screens.feed_my.view_model

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared.paginator.Paginator
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
}