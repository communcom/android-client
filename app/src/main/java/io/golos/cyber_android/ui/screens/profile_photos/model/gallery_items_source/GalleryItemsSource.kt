package io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source

interface GalleryItemsSource {
    /**
     * Returns list of images in the phone gallery, sorted by lastItemDate in descending order
     */
    fun getGalleryImagesUrls(): List<String>
}