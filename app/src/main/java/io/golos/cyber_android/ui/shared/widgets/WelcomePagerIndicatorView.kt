package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.R

class WelcomePagerIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
    }
    private val tmpRectF = RectF()
    private val tmpPaddingList = mutableListOf(0f, 0f)
    private val activeColor = ContextCompat.getColor(context, R.color.blue)
    private val inactiveColor = ContextCompat.getColor(context, R.color.middle_gray)
    private val corners = resources.getDimensionPixelSize(R.dimen.corners_welcome_pager_indicator).toFloat()
    private val padding = resources.getDimensionPixelSize(R.dimen.padding_welcome_pager_indicator).toFloat()

    private var pagesCount = 0
    private var activePage = 0

    fun setupWithViewPager(viewPager: ViewPager2, pagesCount: Int) {
        this.pagesCount = pagesCount
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                activePage = position % pagesCount
                invalidate()
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        for (i in 0..pagesCount) {
            paint.color = if (i == activePage) activeColor else inactiveColor
            val indicatorWidth = (width / pagesCount).toFloat()
            val paddingList = getIndicatorPaddingIndex(i)
            tmpRectF.set(i * indicatorWidth + paddingList[0], 0f, (i + 1) * indicatorWidth - paddingList[1], height.toFloat())
            canvas.drawRoundRect(tmpRectF, corners, corners, paint)
        }
        canvas.restore()
    }

    private fun getIndicatorPaddingIndex(index: Int): List<Float> {
        when(index) {
            0 -> tmpPaddingList.apply {
                set(0, 0f)
                set(1, padding / 2)
            }
            pagesCount - 1 -> tmpPaddingList.apply {
                set(0, padding / 2)
                set(1, 0f)
            }
            else -> tmpPaddingList.apply {
                set(0, padding / 2)
                set(1, padding / 2)
            }
        }
        return tmpPaddingList
    }
}