package io.golos.posts_editor.components.util

import android.graphics.Typeface
import io.golos.domain.use_cases.post.TextStyle

fun Int.mapTypefaceToEditorTextStyle(): TextStyle =
    when(this) {
        Typeface.ITALIC -> TextStyle.ITALIC
        Typeface.BOLD -> TextStyle.BOLD
        Typeface.BOLD_ITALIC -> TextStyle.BOLD_ITALIC
        else -> throw UnsupportedOperationException("This typeface is not supported: $this")
    }
