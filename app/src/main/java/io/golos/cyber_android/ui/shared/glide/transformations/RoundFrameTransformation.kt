package io.golos.cyber_android.ui.shared.glide.transformations

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import io.golos.utils.getColorRes
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
 * A [BitmapTransformation] which puts a round image inside a frame
 */
class RoundFrameTransformation(
    private val context: Context,           // Application context
    @DimenRes
    private val strokeWidthResId: Int,
    @ColorRes
    private val frameColorResId: Int
) : TransformationBase() {

    companion object {
        private const val ID = "io.golos.cyber_android.ui.common.glide.transformations.RoundFrameTransformation"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    private val strokeWidth = context.resources.getDimension(strokeWidthResId)
    private val frameColor = context.resources.getColorRes(frameColorResId)

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun equals(o: Any?): Boolean =
        (o as? RoundFrameTransformation)?.let {
            strokeWidthResId == it.strokeWidthResId && frameColorResId == it.frameColorResId
        } ?: false

    override fun hashCode(): Int {
        var result: Int = 31 * strokeWidthResId
        result = 31 * result + frameColorResId

        return Util.hashCode(ID.hashCode(), result)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer
            .allocate(8)
            .putInt(strokeWidthResId)
            .putInt(frameColorResId)
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

        val combined = getMixedBitmap(base, toTransform)

        if (toTransform != inBitmap) {
            pool.put(toTransform)
        }

        return combined
    }

    private fun getMixedBitmap(base: Bitmap, toTransform: Bitmap): Bitmap {
        val tempCanvas = Canvas(base)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = frameColor
        paint.style = Paint.Style.FILL

        val correctionOffset = strokeWidth/2f

        tempCanvas.drawCircle(base.width/2f, base.height/2f, base.width/2f - correctionOffset, paint)

        val sourceTransformSize = toTransform.width
        val sourceRect = Rect(0, 0, sourceTransformSize, sourceTransformSize)

        val resultTransformSize = base.width - strokeWidth*2

        val resultRect = RectF(
            (base.width-resultTransformSize)/2 + correctionOffset,
            (base.height-resultTransformSize)/2 + correctionOffset,
            base.width - ((base.width-resultTransformSize)/2) - correctionOffset,
            base.height - ((base.height-resultTransformSize)/2) - correctionOffset
        )

        tempCanvas.drawBitmap(toTransform, sourceRect, resultRect, paint)

        return base
    }
}