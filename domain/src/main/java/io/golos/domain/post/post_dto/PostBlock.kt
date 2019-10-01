package io.golos.domain.post.post_dto

data class PostBlock(
    val version: PostFormatVersion,
    val title: String?,
    val type: PostType,
    val content: List<Block>,
    val attachments: AttachmentsBlock?
): Block