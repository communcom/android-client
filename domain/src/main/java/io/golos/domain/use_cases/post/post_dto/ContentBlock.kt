package io.golos.domain.use_cases.post.post_dto

data class ContentBlock(
    val id: String?,
    val type: String?,
    val metadata: PostMetadata,
    val title: String?,
    val content: List<Block>,
    val attachments: AttachmentsBlock?
): Block