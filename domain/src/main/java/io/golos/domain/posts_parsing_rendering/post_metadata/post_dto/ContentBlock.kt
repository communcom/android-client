package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

data class ContentBlock(
    val id: Long?,
    val type: String?,
    val metadata: PostMetadata,
    val title: String?,
    val content: List<Block>,
    val attachments: AttachmentsBlock?
): Block