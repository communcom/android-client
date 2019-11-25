package io.golos.cyber_android.ui.screens.profile_photos.view.grid

interface GalleryGridItemEventsProcessor {
    fun onCameraCellClick()
    fun onPhotoCellClick(selectedImageUri: String, isImageFromCamera: Boolean)
}