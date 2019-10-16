package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_loading.view.*

/**
 * [RecyclerView.ViewHolder] for indicating loading process
 */
class LoadingViewHolder(
    val parent: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)) {
    fun bind(view: View, isLoading: Boolean) {
        view.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}