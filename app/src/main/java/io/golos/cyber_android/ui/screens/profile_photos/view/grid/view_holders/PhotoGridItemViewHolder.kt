package io.golos.cyber_android.ui.screens.profile_photos.view.grid.view_holders

import android.util.Log
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridItemEventsProcessor
import kotlinx.android.synthetic.main.view_profile_photo_grid_photo.view.*

class PhotoGridItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<GalleryGridItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_profile_photo_grid_photo
) {
    private var imageTarget: Target<*>? = null

    override fun init(listItem: VersionedListItem, listItemEventsProcessor: GalleryGridItemEventsProcessor) {
        if(listItem !is PhotoGridItem) {
            return
        }

        imageTarget = Glide
            .with(itemView.context.applicationContext)
            .load(listItem.imageUri)
            .placeholder(R.drawable.placeholder_photo_grid_cell)
            .apply(RequestOptions.centerCropTransform())
            .override(100, 100)
            .into(itemView.photoImage)

        itemView.setOnClickListener { listItemEventsProcessor.onPhotoCellClick(listItem.imageUri, listItem.isImageFromCamera) }
    }

    override fun release() {
        itemView.setOnClickListener(null)

        imageTarget
            ?.let {
                Glide
                    .with(itemView.context.applicationContext)
                    .clear(it)
            }
    }
}