package io.golos.cyber_android.ui.screens.profile_photos.model

import android.net.Uri
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoViewImageInfo
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.io.File

interface ProfilePhotosModel : ModelBase {
    suspend fun createGallery(): List<VersionedListItem>

    fun addCameraImageToGallery(cameraImageUri: Uri): List<VersionedListItem>

    suspend fun saveSelectedPhoto(imageInfo: PhotoViewImageInfo, isNeedCropVisibleArea: Boolean = true): File
}