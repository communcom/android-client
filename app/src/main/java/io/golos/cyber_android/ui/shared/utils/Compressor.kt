package io.golos.cyber_android.ui.shared.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.annotation.FloatRange
import io.golos.data.utils.ImageCompressor
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject

/**
 * Utils class for compressing images before sending it to backend
 */
class ImageCompressorImpl
@Inject
constructor() : ImageCompressor {
    private val MAX_SIZE = 500f


    @Throws(IOException::class)
    override fun compressImageFile(file: File): File {
        if(file.extension == "gif") {       // We don't need to compress gif
            return file
        }

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


    @Throws(IOException::class)
    override fun compressImageFile(
        file: File,
        transX: Float,
        transY: Float,
        rotation: Float
    ): File {
        file.inputStream().use { originalFileStream ->

            val opts = getDecodeOptions(originalFileStream, MAX_SIZE.toInt(), MAX_SIZE.toInt())

            val bitmap = file.inputStream().use {
                BitmapFactory.decodeStream(it, null, opts)
            }

            bitmap!!

            val matrix = Matrix()
            matrix.setRotate(rotation + getOrientationFix(file))

            val x = transX.toInt() / opts.inSampleSize
            val y = transY.toInt() / opts.inSampleSize
            val width = (bitmap.width - x)
            val height = (bitmap.height - y)

            val scaledBitmap = Bitmap.createBitmap(
                bitmap,
                x,
                y,
                width,
                height,
                matrix,
                true
            )


            file.outputStream().use { scaledOutputStream ->
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, scaledOutputStream)
            }
        }
        return file
    }

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
            val height =
                requiredHeightPercent?.times(bitmap.height)?.toInt() ?: (bitmap.height - paddingYPercent).toInt()

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