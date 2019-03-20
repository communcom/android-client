package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.cyber_android.ui.common.posts.PostsDiffCallback
import io.golos.domain.interactors.model.PostModel

class FeedPagerAdapter(
    private val updatesRequestCallBack: ((Tab) -> Unit),
    private val listener: PostsAdapter.Listener): RecyclerView.Adapter<FeedPagerAdapter.ViewHolder>() {

    enum class Tab(@StringRes val title: Int, val index: Int) {
        ALL(R.string.tab_all, 0), MY_FEED(R.string.tab_my_feed, 1)
    }

    private val holders = mutableMapOf<Tab, ViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recyclerView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_pager, parent, false) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(parent.context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(parent.context, R.drawable.divider_post_card)!!)
        })
        return ViewHolder(recyclerView)
    }

    override fun getItemCount() = Tab.values().size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == Tab.ALL.index) {
            holder.recyclerView.adapter = PostsAdapter(PostsDiffCallback(), listener)
            holders[Tab.ALL] = holder
            updatesRequestCallBack.invoke(Tab.ALL)
            restoreCallback?.invoke()
            restoreCallback = null
        }
    }

    fun submitAllList(list: PagedList<PostModel>) {
        holders[Tab.ALL]?.let {
            (it.recyclerView.adapter as PostsAdapter).submitList(list)
        }
    }

    fun saveState(outState: Bundle) {
        holders[Tab.ALL]?.let {
            if (it.recyclerView.layoutManager != null)
                outState.putParcelable(Tab.ALL.name, it.recyclerView.layoutManager?.onSaveInstanceState())

        }
    }

    var restoreCallback : (() -> Unit)? = null

    fun restoreState(savedInstanceState: Bundle) {
        restoreCallback = {
            holders[Tab.ALL]?.let {
                val savedRecyclerLayoutState = savedInstanceState.getParcelable(Tab.ALL.name) as Parcelable
                it.recyclerView.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
            }
        }
    }


    class ViewHolder(val recyclerView: RecyclerView): RecyclerView.ViewHolder(recyclerView)

}