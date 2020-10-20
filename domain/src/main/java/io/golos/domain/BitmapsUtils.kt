package io.golos.domain

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface BitmapsUtils {
    /**
     * Correct image rotation direction
     */
    fun correctOrientation(file: File): File

    fun saveToFile(file: File, source: Bitmap, quality: Int = 50)

    fun isGif(sourceUri: Uri): Boolean
}