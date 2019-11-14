package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ErrorItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ProgressItem
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.screens.my_feed.view.items.CreatePostItem
import io.golos.cyber_android.ui.screens.my_feed.view.items.PostItem
import timber.log.Timber

open class MyFeedAdapter : RecyclerAdapter() {

    private val rvViewPool = RecyclerView.RecycledViewPool()

    var isFullData = false

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    fun updateMyFeedPosts(posts: List<Post>) {
        val postsItems = posts.map {
            val postItem = PostItem(it)
            postItem.setRecycledViewPool(rvViewPool)
            postItem
        }
        Timber.d("paginator:[POST] new items size -> ${postsItems.size}")
        Timber.d("paginator:[POST] saved items size -> ${items.size}")
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        adapterItemsList.addAll(postsItems)
        Timber.d("paginator:[POST] recycler items size -> ${adapterItemsList.size}")
        updateAdapter(adapterItemsList)
    }

    fun updateUser(user: User) {
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val createPostItem = adapterItemsList.find { it is CreatePostItem }
        if (createPostItem == null) {
            adapterItemsList.add(0, CreatePostItem(user))
        } else {
            adapterItemsList[0] = CreatePostItem(user)
        }
        updateAdapter(adapterItemsList)
    }

    fun showLoadingNextPageProgress() {
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val hasProgressItem = adapterItemsList.find { it is ProgressItem } != null
        if (!hasProgressItem) {
            adapterItemsList.add(ProgressItem())
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageProgress() {
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val progressItem = adapterItemsList.find { it is ProgressItem }
        adapterItemsList.remove(progressItem)
        updateAdapter(adapterItemsList)
    }

    fun showLoadingNextPageError() {
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val hasErrorItem = adapterItemsList.find { it is ErrorItem } != null
        if (!hasErrorItem) {
            adapterItemsList.add(
                ErrorItem(
                    View.OnClickListener {
                        onPageRetryLoadingCallback?.invoke()
                    }
                )
            )
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageError() {
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val errorItem = adapterItemsList.find { it is ErrorItem }
        adapterItemsList.remove(errorItem)
        updateAdapter(adapterItemsList)
    }
}