package io.golos.domain.posts_parsing_rendering.post_metadata.editor_output

import androidx.annotation.ColorInt
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle

abstract class SpanInfo<T> (val area: IntRange, val displayValue: T, val value: T)

class StyleSpanInfo(area: IntRange, displayValue: TextStyle, value: TextStyle): SpanInfo<TextStyle>(area, displayValue, value)

/**
 * [displayValue] - color's value as Int
 */
class ColorSpanInfo(area: IntRange, @ColorInt displayValue: Int, value: Int): SpanInfo<Int>(area, displayValue, value)

/**
 * [displayValue] - text of a tag
 */
class TagSpanInfo(area: IntRange, displayValue: String, value: String): SpanInfo<String>(area, displayValue, value)

/**
 * [displayValue] - user's name
 */
class MentionSpanInfo(area: IntRange, displayValue: String, value: String): SpanInfo<String>(area, displayValue, value)

class LinkSpanInfo(area: IntRange, displayValue: LinkInfo, value: LinkInfo): SpanInfo<LinkInfo>(area, displayValue, value)