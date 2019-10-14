package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets.spans

import android.net.Uri
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * Clickable span with some data
 */
abstract class ClickableSpanEx<T>(private val spanData: T): ClickableSpan() {
    override fun onClick(widget: View) = onClick(spanData)

    open fun onClick(spanData: T) {}
}

open class LinkClickableSpan(spanData: Uri, @ColorInt private val textColor: Int): ClickableSpanEx<Uri>(spanData) {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = textColor
        ds.isUnderlineText = true
    }
}