package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders

//class PostImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//    fun bind(imageRowModel: ImageRowModel) {
//        view.progressBar.visibility = View.VISIBLE
//        view.image.setImageDrawable(null)
//        Glide.with(view)
//            .load(imageRowModel.src)
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    view.progressBar.visibility = View.GONE
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    view.progressBar.visibility = View.GONE
//                    return false
//                }
//            })
//            .into(view.image)
//    }
//}