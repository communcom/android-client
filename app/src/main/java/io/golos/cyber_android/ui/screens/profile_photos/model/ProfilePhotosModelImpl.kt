package io.golos.cyber_android.ui.screens.profile_photos.model

import android.net.Uri
import dagger.Lazy
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.CameraGridItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoGridItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoViewImageInfo
import io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source.GalleryItemsSource
import io.golos.cyber_android.ui.screens.profile_photos.model.result_bitmap_calculator.ResultBitmapCalculator
import io.golos.domain.BitmapsUtils
import io.golos.domain.DispatchersProvider
import io.golos.domain.FileSystemHelper
import io.golos.domain.dependency_injection.Clarification
import io.golos.utils.id.IdUtil
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class ProfilePhotosModelImpl
@Inject
constructor(
    @Named(Clarification.IMAGE_URL)
    private val imageUrl: String?,
    private val dispatchersProvider: DispatchersProvider,
    private val galleryItemsSource: GalleryItemsSource,
    private val resultBitmapCalculator: Lazy<ResultBitmapCalculator>,
    private val fileSystemHelper: Lazy<FileSystemHelper>,
    private val bitmapsUtils: Lazy<BitmapsUtils>
) : ModelBaseImpl(),
    ProfilePhotosModel {

    private val galleryItems = mutableListOf<VersionedListItem>()

    override suspend fun createGallery(): List<VersionedListItem> {
        val photoItems = withContext(dispatchersProvider.ioDispatcher) {
            galleryItemsSource.getGalleryImagesUrls()
        }
        .map { PhotoGridItem(IdUtil.generateLongId(), 0, false, false, it, false) }

        galleryItems.add(CameraGridItem())

        if(imageUrl != null) {
            galleryItems.add(PhotoGridItem(IdUtil.generateLongId(), 0, false, false, imageUrl, false))
        }

        galleryItems.addAll(photoItems)

        return galleryItems
    }

    override fun addCameraImageToGallery(cameraImageUri: Uri): List<VersionedListItem> {
        galleryItems.add(1, PhotoGridItem(IdUtil.generateLongId(), 0, false, false, cameraImageUri.toString(), true))
        return galleryItems
    }

    override suspend fun saveSelectedPhoto(imageInfo: PhotoViewImageInfo, isNeedCropVisibleArea: Boolean): File {
        val bitmap  = if(isNeedCropVisibleArea) withContext(dispatchersProvider.calculationsDispatcher) {
            resultBitmapCalculator.get().calculateVisibleArea(imageInfo)
        } else {
            imageInfo.source
        }

        return withContext(dispatchersProvider.ioDispatcher) {
            fileSystemHelper.get().getTempImageFile()
                .also { bitmapsUtils.get().saveToFile(it, bitmap) }
        }
    }
}