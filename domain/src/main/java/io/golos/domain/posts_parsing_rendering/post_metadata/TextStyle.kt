package io.golos.domain.posts_parsing_rendering.post_metadata

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

