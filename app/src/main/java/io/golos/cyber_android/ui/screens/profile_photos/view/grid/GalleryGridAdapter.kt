package io.golos.cyber_android.ui.screens.profile_photos.view.grid

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.profile_photos.dto.CameraGridItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.view_holders.CameraGridItemViewHolder
import io.golos.cyber_android.ui.screens.profile_photos.view.grid.view_holders.PhotoGridItemViewHolder
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class GalleryGridAdapter(
    private val listItemEventsProcessor: GalleryGridItemEventsProcessor
) : VersionedListAdapterBase<GalleryGridItemEventsProcessor>(listItemEventsProcessor, null) {

    protected companion object {
        const val PHOTO = 0
        const val CAMERA = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<GalleryGridItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            PHOTO -> PhotoGridItemViewHolder(parent)
            CAMERA -> CameraGridItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is PhotoGridItem -> PHOTO
            is CameraGridItem -> CAMERA
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }
}