package io.golos.posts_editor.dto.control_metadata

import io.golos.posts_editor.dto.SpanInfo

/**
 * Metadata for a text paragraph
 */
data class ParagraphMetadata(
    val plainText: String,
    val spans: List<SpanInfo<*>>
) : ControlMetadata