package io.golos.cyber_android.ui.shared.utils

import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView

/**
 * [LinkMovementMethod] that allows to manually handle link clicks
 */
class CustomLinkMovementMethod(private val listener: Listener) : LinkMovementMethod() {

    enum class LinkType {
        IMAGE, USERNAME, WEB
    }

    /**
     * List of image links which this Movement method should handle. Other link will be considered as usual web links
     */
    var imageLinks = emptyList<String>()

    override fun onTouchEvent(
        widget: TextView,
        buffer: android.text.Spannable,
        event: MotionEvent
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
                val handled = when {
                    imageLinks.contains(url) -> listener.onLinkClick(url,
                        LinkType.IMAGE
                    )
                    Patterns.USERNAME.matcher(url).matches() -> listener.onLinkClick(url,
                        LinkType.USERNAME
                    )
                    else -> listener.onLinkClick(url, LinkType.WEB)
                }
                return handled || super.onTouchEvent(widget, buffer, event)
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    interface Listener {
        fun onLinkClick(url: String, type: LinkType): Boolean
    }
}
