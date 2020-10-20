package io.golos.cyber_android.ui.shared.glide.transformations

import android.graphics.*
import android.os.Build
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Preconditions
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * A [BitmapTransformation] which rounds the top corners of a bitmap.
 * Based on Glide code
 */
class TopRoundedCornersTransformation(
    private val roundingRadius: Float
) : TransformationBase() {
    companion object {
        private const val ID = "io.golos.cyber_android.ui.common.glide.transformations.TopRoundedCornersTransformation"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }

    // See #738.
    private val modelsRequiringBitmapLock = HashSet(
        listOf(
            // Moto X gen 2
            "XT1085",
            "XT1092",
            "XT1093",
            "XT1094",
            "XT1095",
            "XT1096",
            "XT1097",
            "XT1098",
            // Moto G gen 1
            "XT1031",
            "XT1028",
            "XT937C",
            "XT1032",
            "XT1008",
            "XT1033",
            "XT1035",
            "XT1034",
            "XT939G",
            "XT1039",
            "XT1040",
            "XT1042",
            "XT1045",
            // Moto G gen 2
            "XT1063",
            "XT1064",
            "XT1068",
            "XT1069",
            "XT1072",
            "XT1077",
            "XT1078",
            "XT1079"
        )
    )

    private val bitmapDrawableLock: Lock? = if (modelsRequiringBitmapLock.contains(Build.MODEL))
        ReentrantLock()
    else
        null

    init {
        Preconditions.checkArgument(roundingRadius > 0f, "roundingRadius must be greater than 0.")
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return roundedCorners(pool, toTransform, roundingRadius)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun equals(o: Any?): Boolean {
        if (o is TopRoundedCornersTransformation) {
            val other = o as TopRoundedCornersTransformation?
            return roundingRadius == other!!.roundingRadius
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(
            ID.hashCode(),
            Util.hashCode(roundingRadius)
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer.allocate(4).putFloat(roundingRadius).array()
        messageDigest.update(radiusData)
    }

    private fun roundedCorners(pool: BitmapPool, inBitmap: Bitmap, roundingRadius: Float): Bitmap {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.")

        // Alpha is required for this transformation.
        val safeConfig = getAlphaSafeConfig(inBitmap)
        val toTransform = getAlphaSafeBitmap(pool, inBitmap)
        val result = pool.get(toTransform.width, toTransform.height, safeConfig)

        result.setHasAlpha(true)

        val shader = BitmapShader(
            toTransform, Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader

        bitmapDrawableLock?.lock()
        try {
            val canvas = Canvas(result)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val width = result.width.toFloat()
            val height = result.height.toFloat()

            val path = Path()
            path.moveTo(roundingRadius, 0f)
            path.lineTo(width-roundingRadius, 0f)
            path.quadTo(width, 0f, width, roundingRadius)
            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.lineTo(0f, roundingRadius)
            path.quadTo(0f, 0f, roundingRadius, 0f)

            canvas.drawPath(path,paint)

            canvas.setBitmap(null)
        } finally {
            bitmapDrawableLock?.unlock()
        }

        if (toTransform != inBitmap) {
            pool.put(toTransform)
        }

        return result
    }
}
