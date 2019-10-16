package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_post_comments_title.view.*

class CommentsTitleViewHolder(
    val parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comments_title, parent, false)) {

    fun bind(view: View, count: Long) {
        view.postCommentsTitle.text = String.format(
            view.context.resources.getString(R.string.comments_title_format),
            count
        )
    }
}