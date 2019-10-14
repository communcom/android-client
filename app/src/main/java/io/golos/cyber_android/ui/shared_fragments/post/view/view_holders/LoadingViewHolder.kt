package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_loading.view.*

/**
 * [RecyclerView.ViewHolder] for indicating loading process
 */
class LoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        isLoading: Boolean
    ) {
        view.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}