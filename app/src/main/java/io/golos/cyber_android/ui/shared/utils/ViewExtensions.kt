package io.golos.cyber_android.ui.shared.utils

import android.os.Build
import android.text.Editable
import android.text.Spannable
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import java.util.regex.Pattern

fun FrameLayout.LayoutParams.setBottomMargin(dp: Int) {
    setMargins(0, 0, 0, dp)
}

fun AppCompatTextView.setStyle(@StyleRes styleResId: Int) {
    if (Build.VERSION.SDK_INT < 23) {
        setTextAppearance(context, styleResId)
    } else {
        setTextAppearance(styleResId)
    }
}

fun AppCompatTextView.setDrawableToEnd(@DrawableRes drawableResId: Int) {
    val drawable = ContextCompat.getDrawable(context, drawableResId)
    drawable?.let {
        setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
    }
}

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

fun TextView.colorizeUsernames() {
    colorizeByPattern(this, Patterns.USERNAME)
}

private fun colorizeByPattern(s: Spannable, pattern: Pattern) {
    CustomLinkify.addLinks(s, pattern, null)
}

private fun colorizeByPattern(t: TextView, pattern: Pattern) {
    CustomLinkify.addLinks(t, pattern, null)
}