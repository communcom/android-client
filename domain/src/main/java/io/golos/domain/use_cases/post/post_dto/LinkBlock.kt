package io.golos.domain.use_cases.post.post_dto

import android.net.Uri

data class LinkBlock(
    val id: Long?,
    val content: String,
    val url: Uri
): ParagraphItemBlock