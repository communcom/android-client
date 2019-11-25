package io.golos.cyber_android.ui.screens.profile_photos.dto

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect

/**
 * Source data about resized/swapped image
 */
data class PhotoViewImageInfo (
    val source: Bitmap,
    val imageMatrix: Matrix,
    val drawingRect: Rect
)