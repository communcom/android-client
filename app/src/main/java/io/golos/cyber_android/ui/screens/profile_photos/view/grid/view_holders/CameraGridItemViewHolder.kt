package io.golos.cyber_android.ui.screens.profile_photos.view.grid.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_photos.dto.CameraGridItem
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.GalleryGridItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

class CameraGridItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<GalleryGridItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_profile_photo_grid_camera
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: GalleryGridItemEventsProcessor) {
        if(listItem !is CameraGridItem) {
            return
        }

        itemView.setOnClickListener { listItemEventsProcessor.onCameraCellClick() }
    }

    override fun release() {
        itemView.setOnClickListener(null)
    }
}