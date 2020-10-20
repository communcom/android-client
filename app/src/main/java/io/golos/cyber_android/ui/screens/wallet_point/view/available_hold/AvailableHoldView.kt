package io.golos.cyber_android.ui.screens.wallet_point.view.available_hold

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import io.golos.cyber_android.R

class AvailableHoldView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var drawingRectOuter: RectF
    private lateinit var drawingRectInner: RectF

    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    @ColorInt
    private var innerColorStart = 0
    @ColorInt
    private var innerColorEnd = 0

    @Px
    private var strokeWidth = 0f

    private var innerSizeFactor = 0f        // [0f; 1f]

    init {
        attrs?.let { retrieveAttributes(attrs) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingRectOuter = RectF(0f, 0f, w.toFloat(), h.toFloat())

        calculateInner()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        outerPaint.style = Paint.Style.FILL

        canvas?.drawRoundRect(drawingRectOuter, drawingRectOuter.height()/2, drawingRectOuter.height()/2, outerPaint)

        canvas?.drawRoundRect(drawingRectInner, drawingRectInner.height()/2, drawingRectInner.height()/2, innerPaint)
    }

    fun setInnerSizeFactor(value: Float) {
        innerSizeFactor = value

        if(::drawingRectInner.isInitialized) {
            calculateInner()
            invalidate()
        }
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AvailableHoldView)

        outerPaint.color = typedArray.getColor(R.styleable.AvailableHoldView_outer_color, 0)

        innerColorStart = typedArray.getColor(R.styleable.AvailableHoldView_inner_color_start, 0)
        innerColorEnd = typedArray.getColor(R.styleable.AvailableHoldView_inner_color_end, 0)

        strokeWidth = typedArray.getDimension(R.styleable.AvailableHoldView_stroke_width, 0f)

        typedArray.recycle()
    }

    private fun calculateInner() {
        val innerRight = when {
            innerSizeFactor <= 0f -> drawingRectOuter.left + strokeWidth
            innerSizeFactor > 1f -> (drawingRectOuter.right - strokeWidth)
            else -> (drawingRectOuter.right - strokeWidth)*innerSizeFactor
        }

        drawingRectInner = RectF(
            drawingRectOuter.left + strokeWidth,
            drawingRectOuter.top + strokeWidth,
            innerRight,
            drawingRectOuter.bottom - strokeWidth)

        val innerGradient = LinearGradient(
            drawingRectInner.left,
            drawingRectInner.top,
            drawingRectInner.right,
            drawingRectInner.top,
            innerColorStart,
            innerColorEnd,
            Shader.TileMode.CLAMP)

        innerPaint.shader = innerGradient
    }
}