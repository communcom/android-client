package io.golos.data

import androidx.annotation.FloatRange
import java.io.File
import java.io.IOException

interface ImageCompressor {
    @Throws(IOException::class)
    fun compressImageFile(
        file: File
    ): File

    fun compressImageFile(
        file: File,
        transX: Float,
        transY: Float,
        rotation: Float,
        toSquare: Boolean = false
    ): File

    @Throws(IOException::class)
    fun compressImageFile(
        file: File,
        @FloatRange(from = 0.0, to = 1.0)
        paddingXPercent: Float,
        @FloatRange(from = 0.0, to = 1.0)
        paddingYPercent: Float,
        @FloatRange(from = 0.0, to = 1.0)
        requiredWidthPercent: Float? = null,
        @FloatRange(from = 0.0, to = 1.0)
        requiredHeightPercent: Float? = null
    ): File
}