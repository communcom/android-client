package io.golos.domain.post

import android.graphics.Typeface

enum class TextStyle {
    BOLD,
    ITALIC,
    BOLD_ITALIC
}

fun TextStyle.toTypeface(): Int =
    when(this) {
        TextStyle.ITALIC -> Typeface.ITALIC
        TextStyle.BOLD -> Typeface.BOLD
        TextStyle.BOLD_ITALIC -> Typeface.BOLD_ITALIC
    }

