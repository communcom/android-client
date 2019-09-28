package io.golos.posts_editor.components.input.spans.calculators

import android.text.style.ForegroundColorSpan
import io.golos.posts_editor.components.input.spans.spans_worker.SpansWorkerRead
import io.golos.domain.post.editor_output.ColorSpanInfo
import io.golos.domain.post.editor_output.SpanInfo
import kotlin.reflect.KClass

class ColorSpansCalculator(spansReader: SpansWorkerRead): SpansCalculator<Int>(spansReader) {
    fun calculate(area: IntRange, color: Int) = calculate(createSpanInfo(area, color))

    override fun calculateSpanValue(oldValue: Int, newValue: Int): Int? = newValue

    override fun SpanInfo<Int>.copy(newValue: Int): SpanInfo<Int> =
        ColorSpanInfo(area, newValue)

    override fun createSpanInfo(area: IntRange, newValue: Int): SpanInfo<Int> =
        ColorSpanInfo(area, newValue)

    override fun getSpanClass(): KClass<*> = ForegroundColorSpan::class

    override fun getSpanValue(rawSpan: Any): Int = (rawSpan as ForegroundColorSpan).foregroundColor
}