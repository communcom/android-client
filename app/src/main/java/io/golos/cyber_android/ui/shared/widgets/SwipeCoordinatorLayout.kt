@file:Suppress("PackageDirectoryMismatch")

package com.google.android.material.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.golos.cyber_android.application.shared.display_info.DisplayInfoProvider
import timber.log.Timber

class SwipeCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    init {
        setDistanceToTriggerSync(300)
    }

    private var canProcessEvents = false

    private val offsetHelper: ViewOffsetHelper? by lazy { findOffsetHelper() }

    val displayHeight = DisplayInfoProvider.getSizeInPix(context).height

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev!!.action) {
            ACTION_DOWN -> {
                canProcessEvents = ev.y < displayHeight/2
            }
            ACTION_UP -> {
                canProcessEvents = false
            }
        }

        return if(isInTop() && canProcessEvents) {
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