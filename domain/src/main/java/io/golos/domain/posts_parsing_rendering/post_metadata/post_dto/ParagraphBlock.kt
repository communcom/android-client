package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

data class ParagraphBlock(
    val id: Long?,
    val content: List<ParagraphItemBlock>
) : ParagraphItemBlock