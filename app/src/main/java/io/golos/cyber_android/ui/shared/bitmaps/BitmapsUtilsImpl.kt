package io.golos.cyber_android.ui.shared.bitmaps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import io.golos.domain.BitmapsUtils
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


/** */
class BitmapsUtilsImpl
@Inject
constructor(
    private val appContext: Context
): BitmapsUtils {
    /** Correct image rotation direction */
    override fun correctOrientation(file: File): File {
            val degrees = when (getOrientation(file)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            if (degrees != 0f) {
                val source = BitmapFactory.decodeFile(file.absolutePath)
                val rotated = rotate(source, degrees)
                file.delete()
                saveToFile(file, rotated)
            }

            return file
        }

    override fun saveToFile(file: File, source: Bitmap, quality: Int) {
        FileOutputStream(file).use { stream ->
            source.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
    }

    override fun isGif(sourceUri: Uri): Boolean =
        appContext.contentResolver.openInputStream(sourceUri).use { input ->
            input?.let {
                val buffer = ByteArray(3)
                it.read(buffer)
                val type = String(buffer, java.nio.charset.StandardCharsets.UTF_8)
                type == "GIF"
            } ?: false
        }

    /**
     * Get rotation angle for saved bitmap
     * @return ExifInterface.ORIENTATION_
     */
    private fun getOrientation(bitmapFile: File) =
        try {
            ExifInterface(bitmapFile.absolutePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        } catch (e: IOException) {
            Timber.e(e)
            ExifInterface.ORIENTATION_UNDEFINED
        }

    /** */
    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}