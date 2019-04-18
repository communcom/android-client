package io.golos.cyber_android.ui.screens.post.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.domain.interactors.model.TextRowModel
import kotlinx.android.synthetic.main.item_content_text.view.*

class PostTextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(textRowModel: TextRowModel) {
        view.text.text = textRowModel.text
    }
}