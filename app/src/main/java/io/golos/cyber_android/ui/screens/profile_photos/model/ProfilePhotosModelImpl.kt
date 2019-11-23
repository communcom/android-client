package io.golos.cyber_android.ui.screens.profile_photos.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.CameraGridItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source.GalleryItemsSource
import io.golos.domain.DispatchersProvider
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfilePhotosModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val galleryItemsSource: GalleryItemsSource
) : ModelBaseImpl(),
    ProfilePhotosModel {

    private val galleryItems = mutableListOf<VersionedListItem>()

    override suspend fun createGallery(): List<VersionedListItem> {
        val photoItems = withContext(dispatchersProvider.ioDispatcher) {
            galleryItemsSource.getGalleryImagesUrls()
        }
        .map { PhotoGridItem(IdUtil.generateLongId(), 0, it) }

        galleryItems.add(CameraGridItem(IdUtil.generateLongId(), 0))
        galleryItems.addAll(photoItems)

        return galleryItems
    }
}