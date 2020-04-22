package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

data class PostMetadata(
    val version: DocumentFormatVersion,
    val type: PostType
)