package io.golos.cyber_android.ui.screens.my_feed.view.list

import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ErrorItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ProgressItem
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.screens.my_feed.view.items.CreatePostItem
import io.golos.cyber_android.ui.screens.my_feed.view.items.PostItem

open class MyFeedAdapter : RecyclerAdapter() {

    private val rvViewPool = RecyclerView.RecycledViewPool()

    var isFullData = false

    var nextPageCallback: (() -> Unit)? = null

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    fun updateMyFeedPosts(posts: List<Post>) {
        val posts = posts.map {
            val postItem = PostItem(it)
            postItem.setRecycledViewPool(rvViewPool)
            postItem
        }
        val adapterItemsList = ArrayList<RecyclerItem>()

        if (items.isNotEmpty() && items[0] is CreatePostItem) {
            adapterItemsList.add(items[0])
        }
        adapterItemsList.addAll(posts)
        if (items.isNotEmpty() && (items.last() is ProgressItem || items.last() is ErrorItem)) {
            adapterItemsList.add(items.last())
        }
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (!isFullData && position >= items.size - 10 && !hasPageError() && !hasPageProgress()) {
            nextPageCallback?.invoke()
        }
    }

    private fun hasPageProgress(): Boolean = items.find { it is ProgressItem } != null

    private fun hasPageError() = items.find { it is ErrorItem } != null

    fun showLoadingNextPageProgress(){
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val hasProgressItem = adapterItemsList.find { it is ProgressItem } != null
        if(!hasProgressItem){
            adapterItemsList.add(ProgressItem())
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageProgress(){
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val progressItem = adapterItemsList.find { it is ProgressItem }
        adapterItemsList.remove(progressItem)
        updateAdapter(adapterItemsList)
    }

    fun showLoadingNextPageError(){
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val hasErrorItem = adapterItemsList.find { it is ErrorItem } != null
        if(!hasErrorItem){
            adapterItemsList.add(ErrorItem())
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageError(){
        val adapterItemsList = ArrayList<RecyclerItem>(items)
        val errorItem = adapterItemsList.find { it is ErrorItem }
        adapterItemsList.remove(errorItem)
        updateAdapter(adapterItemsList)
    }
}