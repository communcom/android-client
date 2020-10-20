package io.golos.domain.requestmodel

import android.net.Uri
import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
data class ImageUploadRequest(
    val imageFile: File,
    val localUri: Uri,
    val compressionParams: CompressionParams
) : Identifiable {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _imageFile = imageFile
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_imageFile != other._imageFile) return false

            return true
        }

        override fun hashCode(): Int {
            return _imageFile.hashCode()
        }

    }
}

/**
 * Class represents possible image compression parameters. Those parameters affects
 * image width, height etc
 */
sealed class CompressionParams {

    /**
     * [CompressionParams] that doesn't require any transformations to image before compression
     */
    object DirectCompressionParams : CompressionParams()

    /**
     * [CompressionParams] that should take [[[transX], imageWidth], [[transY], imageHeight]]
     * pixels from bitmap, then rotate it on [rotation] degrees
     */
    data class AbsoluteCompressionParams(val transX: Float, val transY: Float,
                                         val rotation: Float): CompressionParams()

    /**
     * [CompressionParams] that should take [[[paddingXPercent] * imageWidth, imageWidth * [requiredWidthPercent]],
     * [[paddingYPercent] * imageHeight, imageHeight * [requiredHeightPercent]]]
     * pixels from bitmap before compression
     */
    data class RelativeCompressionParams(val paddingXPercent: Float, val paddingYPercent: Float,
                                         val requiredWidthPercent: Float, val requiredHeightPercent: Float): CompressionParams()
}

