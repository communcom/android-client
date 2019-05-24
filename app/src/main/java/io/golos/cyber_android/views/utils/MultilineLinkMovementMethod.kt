package io.golos.cyber_android.views.utils

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView

class MultilineLinkMovementMethod : LinkMovementMethod() {

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

            var lineLength = line
            val lines = widget.text.toString().split("\n")
            for (i in 0..line) {
                lineLength += lines[i].length
            }

            if (off >= lineLength) {
                // Return true so click won't be triggered in the leftover empty space
                return true
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }
}