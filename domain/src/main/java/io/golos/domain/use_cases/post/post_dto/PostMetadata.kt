package io.golos.domain.use_cases.post.post_dto

data class PostMetadata(
    val version: DocumentFormatVersion,
    val type: PostType
)