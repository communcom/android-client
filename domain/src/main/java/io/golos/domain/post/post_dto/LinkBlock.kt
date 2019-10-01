package io.golos.domain.post.post_dto

import android.net.Uri

data class LinkBlock(
    val content: String,
    val url: Uri
): ParagraphItemBlock