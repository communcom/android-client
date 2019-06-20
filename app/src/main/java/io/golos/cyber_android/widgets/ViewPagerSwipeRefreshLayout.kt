package io.golos.cyber_android.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.IdRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.common.extensions.getParentActivity


/**
 * Child of [SwipeRefreshLayout] for working inside [ViewPager2]
 */
class ViewPagerSwipeRefreshLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    private var startX: Float = 0f

    private var pager: ViewPager2? = null

    private val horizontalSwipeThreshold = 40f      // In dips (it's not necessary to use a value from dimens.xml here)
    private var horizontalSwipeThresholdPx: Int = 0

    init {
        attrs
            ?.let { extractViewPagerId(it) }
            ?.let { viewPagerResId ->
                val parentActivity = getParentActivity()!!

                val uiCalculator = parentActivity.serviceLocator.getUICalculator()
                horizontalSwipeThresholdPx = uiCalculator.dpToPixels(horizontalSwipeThreshold)

                pager = parentActivity.findViewById(viewPagerResId)
            }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        pager?.let { pager ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    pager.isUserInputEnabled = false
                }

                MotionEvent.ACTION_MOVE -> {
                    if(Math.abs(event.x - startX) > horizontalSwipeThresholdPx) {
                        pager.isUserInputEnabled = true
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(event)
    }

    @IdRes
    private fun extractViewPagerId(attrs: AttributeSet): Int? {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerSwipeRefreshLayout)

        val viewPagerResId = typedArray.getResourceId(R.styleable.ViewPagerSwipeRefreshLayout_view_pager, 0)

        typedArray.recycle()

        return if(viewPagerResId != 0) viewPagerResId else null
    }
}