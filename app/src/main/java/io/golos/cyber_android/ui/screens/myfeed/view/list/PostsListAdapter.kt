package io.golos.cyber_android.ui.screens.myfeed.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.commun4j.services.model.Post
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.paginator.PaginalAdapter

class PostsListAdapter : PaginalAdapter<Post>() {

    override var items: MutableList<Post> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROGRESS_ERROR -> getProgressErrorViewHolder(parent)
            DATA -> PostViewHolder(parent)
            else -> PostViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            DATA -> {
                val post = items[position]
                (holder as PostViewHolder).bind(post)
            }
        }
    }

    inner class PostViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_content, parent, false)) {

        fun bind(post: Post) {

        }
    }
}