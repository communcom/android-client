package io.golos.cyber_android.ui.screens.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R

class FeedPagerAdapter: RecyclerView.Adapter<FeedPagerAdapter.ViewHolder>() {

    enum class Tabs(@StringRes val title: Int) {
        ALL(R.string.tab_all), MY_FEED(R.string.tab_my_feed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recyclerView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_pager, parent, false) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
        return ViewHolder(recyclerView)
    }

    override fun getItemCount() = Tabs.values().size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(val recyclerView: RecyclerView): RecyclerView.ViewHolder(recyclerView)

}