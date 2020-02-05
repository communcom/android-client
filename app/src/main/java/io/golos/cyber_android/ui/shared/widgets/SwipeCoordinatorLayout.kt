@file:Suppress("PackageDirectoryMismatch")

package com.google.android.material.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class SwipeCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    private val offsetHelper: ViewOffsetHelper? by lazy { findOffsetHelper() }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if(isInTop()) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    private fun findOffsetHelper(): ViewOffsetHelper? {
        val rootLayout = children.first { it is CoordinatorLayout } as CoordinatorLayout
        val appBar = rootLayout.children.first() as AppBarLayout

        val lParams = appBar.layoutParams as CoordinatorLayout.LayoutParams

        val behavior = (lParams.behavior as AppBarLayout.Behavior)

        val offsetField = ViewOffsetBehavior::class.java.getDeclaredField("viewOffsetHelper")
        offsetField.isAccessible = true
        return offsetField.get(behavior) as ViewOffsetHelper
    }

    private fun isInTop() = offsetHelper?.topAndBottomOffset == 0
}