package io.golos.posts_editor.components.util

import android.graphics.Typeface
import io.golos.posts_editor.models.EditorTextStyle

fun Int.mapTypefaceToEditorTextStyle(): EditorTextStyle =
    when(this) {
        Typeface.ITALIC -> EditorTextStyle.ITALIC
        Typeface.BOLD -> EditorTextStyle.BOLD
        Typeface.BOLD_ITALIC -> EditorTextStyle.BOLD_ITALIC
        else -> throw UnsupportedOperationException("This typeface is not supported: $this")
    }
