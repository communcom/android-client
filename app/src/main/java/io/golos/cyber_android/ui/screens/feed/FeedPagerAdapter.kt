package io.golos.cyber_android.ui.screens.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.posts.PostsAdapter
import io.golos.domain.interactors.model.PostModel

class FeedPagerAdapter(val allFeed: MutableList<PostModel>): RecyclerView.Adapter<FeedPagerAdapter.ViewHolder>() {

    enum class Tabs(@StringRes val title: Int) {
        ALL(R.string.tab_all), MY_FEED(R.string.tab_my_feed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recyclerView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_pager, parent, false) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(parent.context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(parent.context, R.drawable.divider_post_card)!!)
        })
        return ViewHolder(recyclerView)
    }

    override fun getItemCount() = Tabs.values().size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.recyclerView.adapter = PostsAdapter(allFeed, object : PostsAdapter.Listener {
                override fun onSendClick(post: PostModel, comment: String, upvoted: Boolean, downvoted: Boolean) {

                }
            })
        }
    }

    class ViewHolder(val recyclerView: RecyclerView): RecyclerView.ViewHolder(recyclerView)

}