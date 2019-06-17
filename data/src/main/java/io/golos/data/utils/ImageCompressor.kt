package io.golos.data.utils

import androidx.annotation.FloatRange
import java.io.File
import java.io.IOException

interface ImageCompressor {

    /**
     * Compresses image file and returns new compressed file (which will rewrite original file)
     * @param file original image file
     * @return compressed image file, rewrites original file
     */
    @Throws(IOException::class)
    fun compressImageFile(
        file: File
    ): File

    /**
     * Compresses image file and returns new compressed file (which will rewrite original file)
     * @param file original image file
     * @param transX padding on x axis
     * @param transY padding on y axis
     * @param rotation degrees on which image should be rotated
     * @param toSquare should image be cropped to squre by least dimension
     * @return compressed image file, rewrites original file
     * */
    fun compressImageFile(
        file: File,
        transX: Float,
        transY: Float,
        rotation: Float,
        toSquare: Boolean = false
    ): File

    /**
     * Compresses image file and returns new compressed file (which will rewrite original file)
     * @param file original image file
     * @param paddingXPercent padding on x axis in % of original image width
     * @param paddingYPercent padding on y axis in % of original image height
     * @param requiredWidthPercent required image width in % of original image width
     * @param requiredHeightPercent required image height in % of original image height
     * @return compressed image file, rewrites original file
     * */
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