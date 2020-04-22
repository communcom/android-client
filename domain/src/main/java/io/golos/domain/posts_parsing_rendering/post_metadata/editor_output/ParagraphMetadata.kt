package io.golos.domain.posts_parsing_rendering.post_metadata.editor_output

/**
 * Metadata for a text paragraph
 */
data class ParagraphMetadata(
    val plainText: String,
    val spans: List<SpanInfo<*>>
) : ControlMetadata