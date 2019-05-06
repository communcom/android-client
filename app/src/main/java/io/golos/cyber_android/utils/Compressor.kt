package io.golos.cyber_android.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.IOException

object Compressor {

    private const val MAX_SIZE = 500f

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
            matrix.postRotate(rotation)

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
}