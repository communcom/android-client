package io.golos.cyber_android.ui.screens.profile_photos.model.result_bitmap_calculator

import android.graphics.Bitmap
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoViewImageInfo

interface ResultBitmapCalculator {
    /**
     * Returns preview visible area as a bitmap
     */
    suspend fun calculateVisibleArea(imageInfo: PhotoViewImageInfo): Bitmap
}