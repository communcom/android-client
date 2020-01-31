package io.golos.cyber_android.ui.shared.spans

import android.text.Selection
import android.text.Spannable
import android.text.method.BaseMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView
import timber.log.Timber

/**
 * A movement method that traverses links in the text buffer and fires clicks. Unlike
 * {@link LinkMovementMethod}, this will not consume touch events outside {@link ClickableSpan}s.
 */
object ClickableMovementMethod : BaseMovementMethod() {

    override fun canSelectArbitrarily(): Boolean {
        return false
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {

        val action = event.actionMasked
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {

            val eventX = event.x.toInt()
            var x = eventX

            val eventY = event.y.toInt()
            var y = eventY


            val paddingTop = widget.totalPaddingTop
            val paddingLeft = widget.totalPaddingLeft
            val paddingBottom = widget.totalPaddingBottom
            val totalPaddingRight = widget.totalPaddingRight

            val height = widget.height
            val width = widget.width

            Timber.d("click")

            if (eventX > width - totalPaddingRight ||
                eventX < paddingLeft ||
                eventY > height - paddingBottom ||
                eventY < paddingTop
            ) {
                //Click on paddings
                return false
            }

            x -= paddingLeft
            y -= paddingTop
            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            if (off >= widget.text.length) {
                //Click on empty space
                return false
            }
            val link = buffer.getSpans(off, off, ClickableSpan::class.java)
            if (link.size > 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                } else {
                    Selection.setSelection(
                        buffer, buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])
                    )
                }
                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }

        return false
    }

    override fun initialize(widget: TextView, text: Spannable) {
        Selection.removeSelection(text)
    }
}