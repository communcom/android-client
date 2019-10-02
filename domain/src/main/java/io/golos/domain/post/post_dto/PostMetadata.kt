package io.golos.domain.post.post_dto

data class PostMetadata(
    val version: PostFormatVersion,
    val type: PostType
)