package io.golos.cyber_android.views.utils

import android.text.Editable
import android.text.Spannable
import android.text.util.Linkify
import android.widget.TextView
import java.util.regex.Pattern

fun Editable.colorizeLinks() {
    colorizeByPattern(this, Patterns.WEB_URL)
}

fun Editable.colorizeHashTags() {
    colorizeByPattern(this, Patterns.HASHTAG)
}

fun TextView.colorizeLinks() {
    colorizeByPattern(this, Patterns.WEB_URL)
}

fun TextView.colorizeHashTags() {
    colorizeByPattern(this, Patterns.HASHTAG)
}

private fun colorizeByPattern(s: Spannable, pattern: Pattern) {
    CustomLinkify.addLinks(s, pattern, null)
}

private fun colorizeByPattern(t: TextView, pattern: Pattern) {
    CustomLinkify.addLinks(t, pattern, null)
}