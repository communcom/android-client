package io.golos.cyber_android.ui.shared.spans

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView

open class MovementMethod : LinkMovementMethod() {

    /**
     * @return true it is mean that need handle click on top level
     */
    open fun onEmptyClicked(): Boolean = true

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            if (off >= widget.text.length) {
                // Return true so click won't be triggered in the leftover empty space
                return onEmptyClicked()
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }
}