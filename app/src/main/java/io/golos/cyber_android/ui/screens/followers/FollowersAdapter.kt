package io.golos.cyber_android.ui.screens.followers

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter

class FollowersAdapter: PaginalAdapter<Follower>() {


    override var items: MutableList<Follower> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}