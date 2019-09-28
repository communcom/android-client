package io.golos.domain.post.post_dto

import android.net.Uri
import io.golos.domain.post.LinkType

data class LinkBlock(
    val content: String,
    val url: Uri,
    val type: LinkType,
    val thumbnailUrl: Uri?
): ParagraphItemBlock