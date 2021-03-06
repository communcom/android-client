package io.golos.cyber_android.ui.screens.wallet_shared.carousel

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.pow

class CarouselRecyclerView(
    context: Context,
    attrs: AttributeSet
) : RecyclerView(context, attrs) {

    private companion object {
        const val SCROLL_START = 1
        const val SCROLL_END = 0
        const val SCROLLING_FAST = 2
    }

    private var offsetToCenterScroll = -1
    private var lastItemTag: CarouselItemTag? = null

    private var currentScrollState = -1

    private var onItemSelectedListener: ((String) -> Unit)? = null
    private var lastPostId: String? = null

    fun <T : ViewHolder> addAdapter(newAdapter: Adapter<T>) {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                post {
                    if(offsetToCenterScroll == -1) {
                        val child = getChildAt(0)
                        offsetToCenterScroll = -child.width/2
                    }

                    val sidePadding = (width / 2) - (getChildAt(0).width / 2)
                    setPadding(sidePadding, 0, sidePadding, 0)
                    scrollToAbsolutePosition(0)

                    addOnScrollListener(object : OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            onScrollChanged()
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            if(newState == SCROLL_END) {
                                lastItemTag?.let {
                                    scrollToAbsolutePosition(it.position)

                                    if(currentScrollState == SCROLLING_FAST || currentScrollState == SCROLL_START) {
                                        postOnItemSelectedEvent(it.id)
                                    }
                                }
                            }
                            currentScrollState = newState
                        }
                    })
                }
            }
        })
        adapter = newAdapter
    }

    fun setUp(position: Int, onItemSelectedListener: ((String) -> Unit)?) {
        post{
            val startPosition = (adapter as CarouselAdapter).recalculatePosition(position)
            scrollToAbsolutePosition(startPosition)
            setOnItemSelectedListener(onItemSelectedListener)
        }
    }

    override fun scrollToPosition(position: Int) =
        scrollToAbsolutePosition((adapter as CarouselAdapter).recalculatePosition(position))

    override fun smoothScrollToPosition(position: Int) {
        val recalculatedPosition = (adapter as CarouselAdapter).recalculatePosition(position)

        val scroller = object : LinearSmoothScroller(context) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return 0
            }
        }
        scroller.targetPosition = recalculatedPosition
        (layoutManager as LinearLayoutManager).startSmoothScroll(scroller)
    }

    fun setOnItemSelectedListener(listener: ((String) -> Unit)?) {
        onItemSelectedListener = listener
    }

    private fun onScrollChanged() {
        post {
            val parentCenterX = width / 2

            var maxScale = Float.MIN_VALUE
            var maxScaleChildTag: CarouselItemTag? = null

            (0 until childCount).forEach { position ->
                val child = getChildAt(position)

                val childCenterX = child.left + child.width/2
                val scaleValue = getGaussianScale(childCenterX, parentCenterX)

                val alpha = scaleValue - 1f

                child.scaleX = scaleValue
                child.scaleY = scaleValue
                child.alpha = alpha

                if(scaleValue > maxScale) {
                    maxScale = scaleValue
                    maxScaleChildTag = child.tag as CarouselItemTag
                }
            }

            if(maxScaleChildTag != lastItemTag) {
                lastItemTag = maxScaleChildTag

                if(currentScrollState != SCROLLING_FAST) {
                    postOnItemSelectedEvent(maxScaleChildTag!!.id)
                }
            }
        }
    }

    private fun getGaussianScale(childCenterX: Int, parentCenterX: Int): Float {
        val minScaleOffset = 1f
        val scaleFactor = 1f
        val spreadFactor = 150.0

        return (Math.E.pow(
            -(childCenterX - parentCenterX.toDouble()).pow(2.0) / (2 * spreadFactor.pow(2.0))
        ) * scaleFactor + minScaleOffset).toFloat()
    }

    private fun postOnItemSelectedEvent(id: String) {
        if(lastPostId == id) {
            return
        }
        lastPostId = id

        onItemSelectedListener?.invoke(id)
    }

    private fun scrollToAbsolutePosition(position: Int) {
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offsetToCenterScroll)
    }
}