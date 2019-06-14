package io.golos.cyber_android.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.annotation.FloatRange
import io.golos.data.ImageCompressor
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Utils class for compressing images before sending it to backend
 */
object ImageCompressorImpl: ImageCompressor {

    private const val MAX_SIZE = 500f

    /**
     * Compresses image file and returns new compressed file (which will rewrite original file)
     * @param file original image file
     * @return compressed image file, rewrites original file
     * */
    @Throws(IOException::class)
    override fun compressImageFile(
        file: File
    ): File {
        file.inputStream().use { originalFileStream ->

            val opts = getDecodeOptions(originalFileStream, MAX_SIZE.toInt(), MAX_SIZE.toInt())

            val bitmap = file.inputStream().use {
                BitmapFactory.decodeStream(it, null, opts)
            }

            bitmap!!

            val matrix = Matrix()
            matrix.setRotate(getOrientationFix(file))

            val x = 0
            val y = 0
            val width = (bitmap.width)
            val height = (bitmap.height)

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
    override fun compressImageFile(
        file: File,
        transX: Float,
        transY: Float,
        rotation: Float,
        toSquare: Boolean
    ): File {
        file.inputStream().use { originalFileStream ->

            val opts = getDecodeOptions(originalFileStream, MAX_SIZE.toInt(), MAX_SIZE.toInt())

            val bitmap = file.inputStream().use {
                BitmapFactory.decodeStream(it, null, opts)
            }

            bitmap!!

            val matrix = Matrix()
            matrix.setRotate(rotation + getOrientationFix(file))

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
    override fun compressImageFile(
        file: File,
        @FloatRange(from = 0.0, to = 1.0)
        paddingXPercent: Float,
        @FloatRange(from = 0.0, to = 1.0)
        paddingYPercent: Float,
        @FloatRange(from = 0.0, to = 1.0)
        requiredWidthPercent: Float?,
        @FloatRange(from = 0.0, to = 1.0)
        requiredHeightPercent: Float?
    ): File {
        file.inputStream().use { originalFileStream ->
            val opts = getDecodeOptions(originalFileStream, MAX_SIZE.toInt(), MAX_SIZE.toInt())

            val bitmap = file.inputStream().use {
                BitmapFactory.decodeStream(it, null, opts)
            }
            bitmap!!

            val x = (paddingXPercent * bitmap.width).toInt()
            val y = (paddingYPercent * bitmap.height).toInt()
            val width = requiredWidthPercent?.times(bitmap.width)?.toInt() ?: (bitmap.width - paddingXPercent).toInt()
            val height = requiredHeightPercent?.times(bitmap.height)?.toInt() ?: (bitmap.height - paddingYPercent).toInt()

            val matrix = Matrix()
            matrix.setRotate(getOrientationFix(file))

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

    private fun getDecodeOptions(
        fis: FileInputStream,
        reqWidth: Int,
        reqHeight: Int
    ): BitmapFactory.Options {
        return BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(fis, null, this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun getOrientationFix(file: File): Float {
        return when (ExifInterface(file.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) {
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            else -> 0
        }.toFloat()
    }
}