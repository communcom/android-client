@file:Suppress("PackageDirectoryMismatch")

package com.google.android.material.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children

class SwipeCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var isSwipeEnabled = true

    private var isSwipeTriggered = false
    private var isSwipeInProgress = false

    private var startY = 0f

    private val swipeThreshold = 200f            // To the options and use [dp]

    private val offsetHelper: ViewOffsetHelper? by lazy { findOffsetHelper() }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        if(!isSwipeEnabled || ev == null) {
            return false
        }

        when(ev.action) {
            ACTION_DOWN -> {
                if(!isSwipeTriggered && isInTop()) {
                    Log.d("TEST_TOUCH", "onInterceptTouchEvent->ACTION_DOWN, the swipe is triggered")
                    isSwipeTriggered = true

                    startY = ev.y
                }
            }

            ACTION_MOVE -> {
                if(isSwipeTriggered) {
                    if(ev.y - startY > swipeThreshold) {
                        isSwipeInProgress = true
                        Log.d("TEST_TOUCH", "onInterceptTouchEvent->ACTION_MOVE, the swipe in progress")
                        // Show the indicator
                    }
                }

                return isSwipeInProgress
            }

            ACTION_UP -> {
                Log.d("TEST_TOUCH", "onInterceptTouchEvent->ACTION_UP, complete")

                isSwipeTriggered = false
                isSwipeInProgress = false
                startY = 0f
            }

            ACTION_CANCEL -> {
                Log.d("TEST_TOUCH", "onInterceptTouchEvent->ACTION_CANCEL, complete")

                isSwipeTriggered = false
                isSwipeInProgress = false
                startY = 0f
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) {
            return super.onTouchEvent(event)
        }

        when(event.action) {
            ACTION_MOVE -> {
                Log.d("TEST_TOUCH", "onTouchEvent->ACTION_MOVE, move the indicator")
                // Move the indicator
            }

            ACTION_UP -> {
                Log.d("TEST_TOUCH", "onTouchEvent->ACTION_UP")

                if(isSwipeEnabled) {
                    Log.d("TEST_TOUCH", "onTouchEvent->ACTION_UP, send the event")
                    // send event
                }
                // Remove the indicators

                Log.d("TEST_TOUCH", "onTouchEvent->ACTION_UP, complete")

                isSwipeTriggered = false
                isSwipeInProgress = false
                startY = 0f
            }

            ACTION_CANCEL -> {
                Log.d("TEST_TOUCH", "onTouchEvent->ACTION_CANCEL remove the indicator")

                // Remove the indicators

                Log.d("TEST_TOUCH", "onTouchEvent->ACTION_CANCEL, complete")

                isSwipeTriggered = false
                isSwipeInProgress = false
                startY = 0f
            }
        }

        return true
    }

    private fun findOffsetHelper(): ViewOffsetHelper? {
        val rootLayout = children.first() as CoordinatorLayout
        val appBar = rootLayout.children.first() as AppBarLayout

        val lParams = appBar.layoutParams as CoordinatorLayout.LayoutParams

        val behavior = (lParams.behavior as AppBarLayout.Behavior)

        val offsetField = ViewOffsetBehavior::class.java.getDeclaredField("viewOffsetHelper")
        offsetField.isAccessible = true
        return offsetField.get(behavior) as ViewOffsetHelper
    }

    /**
     * The layout scrolling is in a top position
     */
    private fun isInTop() = offsetHelper?.topAndBottomOffset == 0
}
