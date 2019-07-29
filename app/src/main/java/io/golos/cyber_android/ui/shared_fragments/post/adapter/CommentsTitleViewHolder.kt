package io.golos.cyber_android.ui.shared_fragments.post.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_post_comments_title.view.*

class CommentsTitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(count: Long) {
        view.postCommentsTitle.text = String.format(
            view.context.resources.getString(R.string.comments_title_format),
            count
        )
    }
}