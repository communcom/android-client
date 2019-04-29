package io.golos.cyber_android.views.utils

import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView

/**
 * [LinkMovementMethod] that allows to manually handle link clicks
 */
class CustomLinkMovementMethod(private val listener: Listener) : LinkMovementMethod() {

    /**
     * List of image links which this Movement method should handle. Other link will be considered as usual web links
     */
    var imageLinks = emptyList<String>()

    override fun onTouchEvent(
        widget: TextView,
        buffer: android.text.Spannable,
        event: android.view.MotionEvent
    ): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(off, off, URLSpan::class.java)
            if (link.isNotEmpty()) {
                val url = link[0].url
                val handled = if (imageLinks.contains(url))
                    listener.onImageLinkClicked(url)
                else
                    listener.onWebLinkClicked(url)
                return if (handled) {
                    true
                } else super.onTouchEvent(widget, buffer, event)
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    interface Listener {
        fun onImageLinkClicked(url: String): Boolean
        fun onWebLinkClicked(url: String): Boolean
    }
}
