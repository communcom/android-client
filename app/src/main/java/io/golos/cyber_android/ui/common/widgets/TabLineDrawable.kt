package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R

class TabLineDrawable(private val context: Context) : Drawable() {


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var boundsRect: Rect? = null

    private val lineWidth: Float = context.resources.getDimensionPixelSize(R.dimen.tab_line_width).toFloat()

    private val lineHeight: Float = context.resources.getDimensionPixelSize(R.dimen.tab_line_height).toFloat()

    init {
        paint.color = ContextCompat.getColor(context, R.color.blue)
        paint.strokeWidth = lineHeight
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun draw(canvas: Canvas) {
        boundsRect?.let {
            val left = it.left.toFloat()
            val right = it.right.toFloat()
            val halfWidth = (right - left) / 2
            val halfLineWidth = lineWidth / 2
            canvas.drawLine(
                left + halfWidth - halfLineWidth,
                (it.bottom - lineHeight),
                left + halfWidth + halfLineWidth,
                (it.bottom - lineHeight), paint
            )
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        this.boundsRect = bounds
        super.onBoundsChange(bounds)
    }
}