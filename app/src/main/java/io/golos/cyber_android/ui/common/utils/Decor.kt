package io.golos.cyber_android.ui.common.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R

/**
 * Similar to [DividerItemDecoration] but can also draw divider over first item
 */
class TopDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.divider_post_card)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = 0
        val right = parent.width

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildLayoutPosition(child)

            var bottom = child.bottom
            var top = bottom - mDivider!!.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)

            if (position == 0) {
                bottom = child.top
                top = bottom - mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val height = mDivider!!.intrinsicHeight
        val position = parent.getChildLayoutPosition(view)

        outRect.top = if (position == 0) height else 0
        outRect.bottom = height
    }
}

class DividerPostDecoration(context: Context) : DividerItemDecoration(context, VERTICAL) {

    private val divider = ContextCompat.getDrawable(context, R.drawable.divider_post_card)

    init {
        divider?.let { drawable ->
            setDrawable(drawable)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val height = divider!!.intrinsicHeight
        val position = parent.getChildLayoutPosition(view)

        outRect.bottom = height

        if (position == 0) {
            outRect.top = height
        }
    }

}