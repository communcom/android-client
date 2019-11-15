package io.golos.cyber_android.ui.common.glide

import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Preconditions
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
 * A [BitmapTransformation] which puts a round image inside a frame with persentage color indicator
 */
class PercentageRoundFrameTransform(
    private val context: Context,           // Application context
    private val innerImageSize: Float,      // [0:1]
    private val percentage: Float,          // [0:1]
    @ColorRes
    private val percentAreaColor: Int,
    @DrawableRes
    private val frameTemplate: Int
) : BitmapTransformation() {
    companion object {
        private const val ID = "com.example.vectorframe.glide.PercentageRoundFrameTransform"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    init {
        Preconditions.checkArgument(innerImageSize > 0 && innerImageSize <= 1, "innerImageSize must be in [0; 1] interval")
        Preconditions.checkArgument(percentage in 0f..1f, "percentage must be in [0; 1] interval")
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun equals(o: Any?): Boolean {
        if (o is PercentageRoundFrameTransform) {
            val other = o as PercentageRoundFrameTransform?

            return innerImageSize == other!!.innerImageSize &&
                    percentage == other.percentage &&
                    percentAreaColor == other.percentAreaColor
        }
        return false
    }

    override fun hashCode(): Int {
        var result: Int = 31 * (innerImageSize*100).toInt()
        result = 31 * result + (percentage*100).toInt()
        result = 31 * result + percentAreaColor
        result = 31 * result + frameTemplate

        return Util.hashCode(ID.hashCode(), result)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer
            .allocate(16)
            .putFloat(innerImageSize)
            .putFloat(percentage)
            .putInt(percentAreaColor)
            .putInt(frameTemplate)
            .array()
        messageDigest.update(radiusData)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return addFrameToSource(pool, toTransform)
    }

    private fun addFrameToSource(pool: BitmapPool, inBitmap: Bitmap): Bitmap {
        // Alpha is required for this transformation.
        val safeConfig = getAlphaSafeConfig(inBitmap)
        val toTransform = getAlphaSafeBitmap(pool, inBitmap)

        val base = pool.get(toTransform.width, toTransform.height, safeConfig)

        val size = toTransform.width

        val frameBitmap = getFrameBitmap(size, safeConfig)
        val mask = getMaskBitmap(size, safeConfig)
        val combined = getMixedBitmap(base, frameBitmap, mask, toTransform)

        if (toTransform != inBitmap) {
            pool.put(toTransform)
        }

        return combined
    }

    private fun getAlphaSafeConfig(inBitmap: Bitmap): Bitmap.Config {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Avoid short circuiting the sdk check.
            if (Bitmap.Config.RGBA_F16 == inBitmap.config) { // NOPMD
                return Bitmap.Config.RGBA_F16
            }
        }

        return Bitmap.Config.ARGB_8888
    }

    private fun getAlphaSafeBitmap(pool: BitmapPool, maybeAlphaSafe: Bitmap): Bitmap {
        val safeConfig = getAlphaSafeConfig(maybeAlphaSafe)
        if (safeConfig == maybeAlphaSafe.config) {
            return maybeAlphaSafe
        }

        val argbBitmap = pool.get(maybeAlphaSafe.width, maybeAlphaSafe.height, safeConfig)
        Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0f /*left*/, 0f /*top*/, null /*paint*/)

        // We now own this Bitmap. It's our responsibility to replace it in the pool outside this method
        // when we're finished with it.
        return argbBitmap
    }

    private fun getFrameBitmap(size: Int, config: Bitmap.Config): Bitmap {
        val drawable = ContextCompat.getDrawable(context, frameTemplate)!!

        val bitmap = Bitmap.createBitmap(size, size, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getMaskBitmap(size: Int, config: Bitmap.Config): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, config)

        val canvas = Canvas(bitmap)

        val arcRect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        val arcPaint = Paint().apply {
            color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(percentAreaColor, null)
            } else {
                @Suppress("DEPRECATION")
                context.resources.getColor(percentAreaColor)
            }
        }

        canvas.drawArc(arcRect, -90f, 360*percentage, true, arcPaint)

        return bitmap
    }

    private fun getMixedBitmap(base: Bitmap, frame: Bitmap, mask: Bitmap, toTransform: Bitmap): Bitmap {
        val tempCanvas = Canvas(base)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

        tempCanvas.drawBitmap(frame, 0f, 0f, null)
        tempCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null

        val sourceTransformSize = toTransform.width
        val sourceRect = Rect(0, 0, sourceTransformSize, sourceTransformSize)

        val resultTransformSize = base.width * innerImageSize
        val resultRect = RectF(
            (base.width-resultTransformSize)/2,
            (base.height-resultTransformSize)/2,
            base.width - ((base.width-resultTransformSize)/2),
            base.height - ((base.height-resultTransformSize)/2)
        )

        tempCanvas.drawBitmap(toTransform, sourceRect, resultRect, null)

        return base
    }
}