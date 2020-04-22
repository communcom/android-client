package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import android.net.Uri

data class LinkBlock(
    val id: Long?,
    val content: String,
    val url: Uri
): ParagraphItemBlock