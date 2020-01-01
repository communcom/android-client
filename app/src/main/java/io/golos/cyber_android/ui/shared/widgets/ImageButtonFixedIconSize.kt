package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.annotation.Px
import io.golos.cyber_android.R

/**
 * Image button with explicitly set icon size
 */
class ImageButtonFixedIconSize
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.imageButtonStyle
) : ImageButton(context, attrs, defStyleAttr) {

    private lateinit var icon: Drawable

    @Px
    private var iconWidth: Int = 0
    @Px
    private var iconHeight: Int = 0

    private var tint: ColorStateList? = null

    init {
        scaleType = ScaleType.FIT_XY
        attrs?.let { retrieveAttributes(it) }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top

        val horizontalPadding = if(width > iconWidth) (width - iconWidth) / 2 else 0
        val verticalPadding = if(height > iconHeight) (height - iconHeight) / 2 else 0

        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

        tint?.let { icon.setTintList(it) }
        setImageDrawable(icon)

        super.onLayout(changed, left, top, right, bottom)
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonFixedIconSize)

        icon = typedArray.getDrawable(R.styleable.ImageButtonFixedIconSize_imageButton_icon)!!

        val iconWidthTmp = typedArray.getDimension(R.styleable.ImageButtonFixedIconSize_imageButton_iconWidth, 0f)
        val iconHeightTmp = typedArray.getDimension(R.styleable.ImageButtonFixedIconSize_imageButton_iconHeight, 0f)

        val inscribeSize = typedArray.getDimension(R.styleable.ImageButtonFixedIconSize_imageButton_iconInscribeSize, 0f)

        when {
            iconWidthTmp != 0f && iconHeightTmp != 0f -> {
                iconWidth = iconWidthTmp.toInt()
                iconHeight = iconHeightTmp.toInt()
            }

            inscribeSize != 0f -> {
                val inscribeRect = icon.inscribe(inscribeSize.toInt())
                iconWidth = inscribeRect.width()
                iconHeight = inscribeRect.height()
            }
        }

        tint = typedArray.getColorStateList(R.styleable.ImageButtonFixedIconSize_imageButton_tint)

        typedArray.recycle()
    }

    /**
     * Inscribes VectorDrawable into some size
     */
    private fun Drawable.inscribe(targetSize: Int): Rect {
        if(this !is VectorDrawable) {
            throw UnsupportedOperationException("Only VectorDrawable is supported")
        }

        val iconWidth = this.intrinsicWidth
        val iconHeight = this.intrinsicHeight

        // Square icon
        if(iconWidth == iconHeight) {
            return Rect(0, 0, targetSize, targetSize)
        }

        // Vertical-oriented icon
        if(iconWidth < iconHeight) {
            return Rect(0, 0, (targetSize * (iconWidth.toFloat()/iconHeight)).toInt(), targetSize)
        }

        // Horizontal-oriented icon
        val rectHeight = (targetSize * (iconHeight.toFloat()/iconWidth)).toInt()
        val top = (targetSize - rectHeight)/2
        return Rect(0, top, targetSize, top + rectHeight)
    }

}
