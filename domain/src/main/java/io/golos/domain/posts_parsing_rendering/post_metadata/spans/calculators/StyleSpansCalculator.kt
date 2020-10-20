package io.golos.domain.posts_parsing_rendering.post_metadata.spans.calculators

import android.text.style.StyleSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.SpanInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.StyleSpanInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.mapTypefaceToEditorTextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker.SpansWorkerRead
import kotlin.reflect.KClass

class StyleSpansCalculator(spansReader: SpansWorkerRead) : SpansCalculator<TextStyle>(spansReader) {
    fun calculate(area: IntRange, style: TextStyle) =
        if(style == TextStyle.BOLD || style == TextStyle.ITALIC) {
            calculate(createSpanInfo(area, style))
        } else {
            throw UnsupportedOperationException("This style is not supported: $style")
        }

    override fun calculateSpanValue(oldValue: TextStyle, newValue: TextStyle): TextStyle? {
        return when (newValue) {
            TextStyle.BOLD -> {
                when (oldValue) {
                    TextStyle.BOLD -> null
                    TextStyle.ITALIC -> TextStyle.BOLD_ITALIC
                    TextStyle.BOLD_ITALIC -> TextStyle.ITALIC
                }
            }
            TextStyle.ITALIC -> {
                when (oldValue) {
                    TextStyle.BOLD -> TextStyle.BOLD_ITALIC
                    TextStyle.ITALIC -> null
                    TextStyle.BOLD_ITALIC -> TextStyle.BOLD
                }
            }
            else -> throw UnsupportedOperationException("This style is not supported: $newValue")
        }
    }

    override fun SpanInfo<TextStyle>.copy(newValue: TextStyle): SpanInfo<TextStyle> =
        StyleSpanInfo(
            area,
            newValue,
            newValue
        )

    override fun createSpanInfo(area: IntRange, newValue: TextStyle): SpanInfo<TextStyle> =
        StyleSpanInfo(
            area,
            newValue,
            newValue
        )

    override fun getSpanClass(): KClass<*> = StyleSpan::class

    override fun getSpanValue(rawSpan: Any): TextStyle = (rawSpan as StyleSpan).style.mapTypefaceToEditorTextStyle()
}
