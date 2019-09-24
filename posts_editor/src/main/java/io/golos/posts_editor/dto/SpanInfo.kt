package io.golos.posts_editor.dto

import androidx.annotation.ColorInt
import io.golos.posts_editor.models.EditorTextStyle

abstract class SpanInfo<T> (val area: IntRange, val value: T)

class StyleSpanInfo(area: IntRange, value: EditorTextStyle): SpanInfo<EditorTextStyle>(area, value)

/**
 * [value] - color's value as Int
 */
class ColorSpanInfo(area: IntRange, @ColorInt value: Int): SpanInfo<Int>(area, value)

/**
 * [value] - text of a tag
 */
class TagSpanInfo(area: IntRange, value: String): SpanInfo<String>(area, value)

/**
 * [value] - user's name
 */
class MentionSpanInfo(area: IntRange, value: String): SpanInfo<String>(area, value)

class LinkSpanInfo(area: IntRange, value: LinkInfo): SpanInfo<LinkInfo>(area, value)