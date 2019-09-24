package io.golos.posts_editor.components.input.spans.calculators

import io.golos.domain.post_editor.SpanInfo

class SpansIntersections<T> {
    val spansIntersectFull = mutableListOf<SpanInfo<T>>()

    val spansInsideFull = mutableListOf<SpanInfo<T>>()

    val spansInsideLeft = mutableListOf<SpanInfo<T>>()

    val spansInsideRight = mutableListOf<SpanInfo<T>>()

    /**
     * The list of spans that cover completely the new one
     */
    val spansOutsideFull = mutableListOf<SpanInfo<T>>()

    fun hasIntersections() = spansIntersectFull.isNotEmpty() || spansInsideFull.isNotEmpty() || spansInsideLeft.isNotEmpty() ||
            spansInsideRight.isNotEmpty() || spansOutsideFull.isNotEmpty()
}