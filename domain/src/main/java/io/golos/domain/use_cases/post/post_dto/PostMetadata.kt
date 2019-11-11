package io.golos.domain.use_cases.post.post_dto

data class PostMetadata(
    val version: PostFormatVersion,
    val type: PostType
)