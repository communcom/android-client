package io.golos.domain.post_editor

/**
 * Metadata for a text paragraph
 */
data class ParagraphMetadata(
    val plainText: String,
    val spans: List<SpanInfo<*>>
) : ControlMetadata