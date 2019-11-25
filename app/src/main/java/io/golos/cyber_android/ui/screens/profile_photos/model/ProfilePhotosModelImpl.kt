package io.golos.cyber_android.ui.screens.profile_photos.model

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.CameraGridItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source.GalleryItemsSource
import io.golos.domain.BitmapsUtils
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import javax.inject.Inject
import javax.inject.Named

class ProfilePhotosModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val galleryItemsSource: GalleryItemsSource,
    @Named(Clarification.IMAGE_URL)
    private val imageUrl: String?
) : ModelBaseImpl(),
    ProfilePhotosModel {

    private val galleryItems = mutableListOf<VersionedListItem>()

    override suspend fun createGallery(): List<VersionedListItem> {
        val photoItems = withContext(dispatchersProvider.ioDispatcher) {
            galleryItemsSource.getGalleryImagesUrls()
        }
        .map { PhotoGridItem(IdUtil.generateLongId(), 0, it, false) }

        galleryItems.add(CameraGridItem(IdUtil.generateLongId(), 0))

        if(imageUrl != null) {
            galleryItems.add(PhotoGridItem(IdUtil.generateLongId(), 0, imageUrl, false))
        }

        galleryItems.addAll(photoItems)

        return galleryItems
    }

    override fun addCameraImageToGallery(cameraImageUri: Uri): List<VersionedListItem> {
        galleryItems.add(1, PhotoGridItem(IdUtil.generateLongId(), 0, cameraImageUri.toString(), true))
        return galleryItems
    }
}