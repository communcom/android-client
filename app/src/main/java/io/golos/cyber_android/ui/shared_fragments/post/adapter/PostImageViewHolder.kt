package io.golos.cyber_android.ui.shared_fragments.post.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.golos.domain.interactors.model.ImageRowModel
import kotlinx.android.synthetic.main.item_content_image.view.*

class PostImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(imageRowModel: ImageRowModel) {
        view.progressBar.visibility = View.VISIBLE
        view.image.setImageDrawable(null)
        Glide.with(view)
            .load(imageRowModel.src)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    view.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    view.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(view.image)
    }
}