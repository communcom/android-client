package io.golos.domain.use_cases.post.post_dto

@Deprecated("Not need use, use Domain model")
data class PostMetadata(
    val version: PostFormatVersion,
    val type: PostType
)