package io.golos.posts_editor.components.input.spans.calculators

import io.golos.posts_editor.models.EditorTextStyle

abstract class SpanInfo<T> (val area: IntRange, val value: T)

class StyleSpanInfo(area: IntRange, value: EditorTextStyle): SpanInfo<EditorTextStyle>(area, value)

/**
 * [value] - color's value as Int
 */
class ColorSpanInfo(area: IntRange, value: Int): SpanInfo<Int>(area, value)