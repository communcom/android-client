package io.golos.domain.extensions

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle

fun SpannableStringBuilder.setSpan(span: CharacterStyle, interval: IntRange) =
    this.setSpan(span, interval.first, interval.last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

fun SpannableStringBuilder.appendText(text: String): IntRange {
    val start = this.length

    this.append(text)

    return start..this.length
}

fun SpannableStringBuilder.appendSpannedText(text: String, span: CharacterStyle) {
    val interval = this.appendText(text)
    this.setSpan(span, interval)
}