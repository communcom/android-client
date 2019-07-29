package io.golos.cyber_android.ui.shared_fragments.post.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.views.utils.CustomLinkMovementMethod
import io.golos.domain.interactors.model.TextRowModel
import kotlinx.android.synthetic.main.item_content_text.view.*

class PostTextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    init {
        view.text.movementMethod = CustomLinkMovementMethod(object: CustomLinkMovementMethod.Listener {
            override fun onLinkClick(url: String, type: CustomLinkMovementMethod.LinkType): Boolean {
                if (type == CustomLinkMovementMethod.LinkType.WEB) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    if (webIntent.resolveActivity(itemView.context.packageManager) != null) {
                        itemView.context.startActivity(webIntent)
                    }
                }
                return true
            }
        })
    }

    fun bind(textRowModel: TextRowModel) {
        view.text.text = textRowModel.text
    }
}