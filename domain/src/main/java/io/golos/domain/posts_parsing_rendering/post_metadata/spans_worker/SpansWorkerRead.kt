package io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker

import android.text.style.CharacterStyle
import kotlin.reflect.KClass

interface SpansWorkerRead {
    fun <T: CharacterStyle>getAllSpans(spanType: KClass<*>): List<T>

    fun <T: CharacterStyle>getSpans(spanType: KClass<*>, interval: IntRange): List<T>

    fun <T: CharacterStyle>getSpansWithIntervals(spanType: KClass<*>): List<SpanWithRange<T>>

    /**
     * @return null - a span is not found
     */
    fun getSpanUnderPosition(spanType: KClass<*>, cursorPosition: Int): CharacterStyle?

    fun getSpanInterval(span: CharacterStyle): IntRange
}