package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

/**
 * A synthetic structure to render a comment
 */
data class ParagraphSet (
    val paragraphs: List<ParagraphBlock>
): Block