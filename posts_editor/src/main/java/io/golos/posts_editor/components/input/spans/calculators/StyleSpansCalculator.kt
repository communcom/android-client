package io.golos.posts_editor.components.input.spans.calculators

import android.text.style.StyleSpan
import io.golos.posts_editor.components.input.spans.spans_worker.SpansWorkerRead
import io.golos.posts_editor.components.util.mapTypefaceToEditorTextStyle
import io.golos.domain.post_editor.SpanInfo
import io.golos.domain.post_editor.StyleSpanInfo
import io.golos.domain.post_editor.EditorTextStyle
import kotlin.reflect.KClass

class StyleSpansCalculator(spansReader: SpansWorkerRead) : SpansCalculator<EditorTextStyle>(spansReader) {
    fun calculate(area: IntRange, style: EditorTextStyle) =
        if(style == EditorTextStyle.BOLD || style == EditorTextStyle.ITALIC) {
            calculate(createSpanInfo(area, style))
        } else {
            throw UnsupportedOperationException("This style is not supported: $style")
        }

    override fun calculateSpanValue(oldValue: EditorTextStyle, newValue: EditorTextStyle): EditorTextStyle? {
        return when (newValue) {
            EditorTextStyle.BOLD -> {
                when (oldValue) {
                    EditorTextStyle.BOLD -> null
                    EditorTextStyle.ITALIC -> EditorTextStyle.BOLD_ITALIC
                    EditorTextStyle.BOLD_ITALIC -> EditorTextStyle.ITALIC
                }
            }
            EditorTextStyle.ITALIC -> {
                when (oldValue) {
                    EditorTextStyle.BOLD -> EditorTextStyle.BOLD_ITALIC
                    EditorTextStyle.ITALIC -> null
                    EditorTextStyle.BOLD_ITALIC -> EditorTextStyle.BOLD
                }
            }
            else -> throw UnsupportedOperationException("This style is not supported: $newValue")
        }
    }

    override fun SpanInfo<EditorTextStyle>.copy(newValue: EditorTextStyle): SpanInfo<EditorTextStyle> =
        StyleSpanInfo(area, newValue)

    override fun createSpanInfo(area: IntRange, newValue: EditorTextStyle): SpanInfo<EditorTextStyle> =
        StyleSpanInfo(area, newValue)

    override fun getSpanClass(): KClass<*> = StyleSpan::class

    override fun getSpanValue(rawSpan: Any): EditorTextStyle = (rawSpan as StyleSpan).style.mapTypefaceToEditorTextStyle()
}
