package io.golos.cyber_android.ui.shared.extensions

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * Reduces drag sensitivity of [ViewPager2] widget
 */
fun ViewPager2.reduceDragSensitivity() {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true

    val recyclerView = recyclerViewField.get(this) as RecyclerView
    recyclerView.reduceDragSensitivity()
}

/**
 * Reduces drag sensitivity of [ViewPager2] widget
 */
fun RecyclerView.reduceDragSensitivity() {
    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(this) as Int
    touchSlopField.set(this, touchSlop*8)       // "8" was obtained experimentally
}