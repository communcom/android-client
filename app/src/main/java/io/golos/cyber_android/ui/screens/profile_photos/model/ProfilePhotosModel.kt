package io.golos.cyber_android.ui.screens.profile_photos.model

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem

interface ProfilePhotosModel : ModelBase {
    suspend fun createGallery(): List<VersionedListItem>

    fun addCameraImageToGallery(cameraImageUri: Uri): List<VersionedListItem>
}