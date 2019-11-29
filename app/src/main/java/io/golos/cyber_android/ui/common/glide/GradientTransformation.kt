package io.golos.cyber_android.ui.common.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader.TileMode
import androidx.annotation.ColorRes
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.util.Util
import io.golos.cyber_android.ui.common.extensions.getColorRes
import java.nio.ByteBuffer
import java.security.MessageDigest


class GradientTransformation(
    private val context: Context,           // Application context
    @ColorRes
    private val startColor: Int,
    @ColorRes
    private val endColor: Int
) : TransformationBase() {
    companion object {
        private const val ID = "io.golos.cyber_android.ui.common.glide.GradientTransformation"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun equals(o: Any?): Boolean {
        if (o is PercentageRoundVectorFrameTransformation) {
            val other = o as GradientTransformation?

            return startColor == other!!.startColor && endColor == other.endColor
        }
        return false
    }

    override fun hashCode(): Int {
        var result: Int = 31 * startColor
        result = 31 * result + endColor

        return Util.hashCode(ID.hashCode(), result)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer
            .allocate(8)
            .putInt(startColor)
            .putInt(endColor)
            .array()
        messageDigest.update(radiusData)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap =
        addGradientToSource(toTransform)

    private fun addGradientToSource(inBitmap: Bitmap): Bitmap {
        val baseBitmap = Bitmap.createBitmap(inBitmap.width, inBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(baseBitmap)

        canvas.drawBitmap(inBitmap, 0f, 0f, null)

        val shader = LinearGradient(
            inBitmap.width/2f,
            0f,
            baseBitmap.width/2f,
            baseBitmap.height.toFloat(),
            context.resources.getColorRes(startColor),
            context.resources.getColorRes(endColor),
            TileMode.CLAMP)
        val paint = Paint().apply { setShader(shader) }

        canvas.drawRect(0f, 0f, baseBitmap.width.toFloat(), baseBitmap.height.toFloat(), paint)

        return baseBitmap
    }
}