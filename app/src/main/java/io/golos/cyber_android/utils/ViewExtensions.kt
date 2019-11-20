package io.golos.cyber_android.utils

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat


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