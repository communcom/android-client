package io.golos.cyber_android.ui.shared.spans

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView


open class MovementMethod : LinkMovementMethod() {

    /**
     * @return false it is mean that need handle click on top level
     */
    open fun onEmptyClicked(): Boolean = false

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
            val link = buffer.getSpans(off, off, ClickableSpan::class.java)
            return if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0]));
                }
                true
            } else {
                false
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }
}