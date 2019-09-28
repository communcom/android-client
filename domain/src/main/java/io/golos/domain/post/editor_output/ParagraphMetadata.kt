package io.golos.domain.post.editor_output

/**
 * Metadata for a text paragraph
 */
data class ParagraphMetadata(
    val plainText: String,
    val spans: List<SpanInfo<*>>
) : ControlMetadata