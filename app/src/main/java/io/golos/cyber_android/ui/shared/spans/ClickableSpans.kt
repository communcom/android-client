package io.golos.cyber_android.ui.shared.spans

import android.graphics.Typeface
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

open class LinkClickableSpan(
    spanData: Uri, @ColorInt
    private val textColor: Int,
    private val underlineShow: Boolean = true
) : ClickableSpanEx<Uri>(spanData) {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = textColor
        ds.isUnderlineText = underlineShow
    }
}

open class ColorTextClickableSpan(spanData: String, @ColorInt private val textColor: Int): ClickableSpanEx<String>(spanData) {
    override fun updateDrawState(ds: TextPaint) {
        ds.color = textColor
    }
}

open class StyledTextClickableSpan(spanData: String, private val typeface: Typeface): ClickableSpanEx<String>(spanData) {
    override fun updateDrawState(ds: TextPaint) {
        ds.typeface = typeface
    }
}

open class StyledColorTextClickableSpan(
    spanData: String,
    private val typeface: Typeface,
    @ColorInt private val textColor: Int
) : ClickableSpanEx<String>(spanData) {

    override fun updateDrawState(ds: TextPaint) {
        ds.typeface = typeface
        ds.color = textColor
    }
}
