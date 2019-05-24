package io.golos.cyber_android.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.FloatRange
import java.io.File
import java.io.IOException

/**
 * Utils class for compressing images before sending it to backend
 */
object Compressor {

    private const val MAX_SIZE = 500f

    /**
     * Compresses image file and returns new compressed file (which will rewrite original file)
     * @param file original image file
     * @param transX padding on x axis
     * @param transY padding on y axis
     * @param rotation degrees on which image should be rotated
     * @param toSquare should image be cropped to squre by least dimension
     * @return compressed image file, rewrites original file
     * */
    @Throws(IOException::class)
    fun compressImageFile(
        file: File,
        transX: Float,
        transY: Float,
        rotation: Float,
        toSquare: Boolean = false
    ): File {
        file.inputStream().use { originalFileStream ->
            val bitmap = BitmapFactory.decodeStream(
                originalFileStream,
                null,
                BitmapFactory.Options()
            )
            bitmap!!
            val scaleFactor = Math.max(
                bitmap.width / MAX_SIZE,
                bitmap.height / MAX_SIZE
            )

            val matrix = Matrix()
            matrix.postScale(1 / scaleFactor, 1 / scaleFactor)
            matrix.setRotate(rotation)

            val x = transX.toInt()
            val y = transY.toInt()
            val width = (bitmap.width - transX).toInt()
            val height = (bitmap.height - transY).toInt()

            val scaledBitmap = if (!toSquare)
                Bitmap.createBitmap(
                    bitmap,
                    x,
                    y,
                    width, height, matrix, true
                ) else Bitmap.createBitmap(
                bitmap,
                x,
                y,
                Math.min(width, height), Math.min(width, height), matrix, true
            )


            file.outputStream().use { scaledOutputStream ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, scaledOutputStream)
            }
        }
        return file
    }

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
    ): File {
        file.inputStream().use { originalFileStream ->
            val bitmap = BitmapFactory.decodeStream(
                originalFileStream,
                null,
                BitmapFactory.Options()
            )
            bitmap!!

            val x = (paddingXPercent * bitmap.width).toInt()
            val y = (paddingYPercent * bitmap.height).toInt()
            val width = requiredWidthPercent?.times(bitmap.width)?.toInt() ?: (bitmap.width - paddingXPercent).toInt()
            val height = requiredHeightPercent?.times(bitmap.height)?.toInt() ?: (bitmap.height - paddingYPercent).toInt()

            val scaleFactor = Math.max(
                width / MAX_SIZE,
                height / MAX_SIZE
            )

            val matrix = Matrix()
            matrix.postScale(1 / scaleFactor, 1 / scaleFactor)
            matrix.setRotate(0f)

            val scaledBitmap = Bitmap.createBitmap(
                bitmap,
                x,
                y,
                width, height, matrix, true
            )


            file.outputStream().use { scaledOutputStream ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, scaledOutputStream)
            }
        }
        return file
    }
}