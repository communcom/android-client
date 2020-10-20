package io.golos.domain.posts_parsing_rendering.post_metadata.spans.calculators

import android.text.style.ForegroundColorSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.ColorSpanInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.SpanInfo
import io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker.SpansWorkerRead
import kotlin.reflect.KClass

class ColorSpansCalculator(spansReader: SpansWorkerRead): SpansCalculator<Int>(spansReader) {
    fun calculate(area: IntRange, color: Int) = calculate(createSpanInfo(area, color))

    override fun calculateSpanValue(oldValue: Int, newValue: Int): Int? = newValue

    override fun SpanInfo<Int>.copy(newValue: Int): SpanInfo<Int> =
        ColorSpanInfo(
            area,
            newValue,
            newValue
        )

    override fun createSpanInfo(area: IntRange, newValue: Int): SpanInfo<Int> =
        ColorSpanInfo(
            area,
            newValue,
            newValue
        )

    override fun getSpanClass(): KClass<*> = ForegroundColorSpan::class

    override fun getSpanValue(rawSpan: Any): Int = (rawSpan as ForegroundColorSpan).foregroundColor
}