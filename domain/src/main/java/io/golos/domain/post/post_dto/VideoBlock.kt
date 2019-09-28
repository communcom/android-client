package io.golos.domain.post.post_dto

import android.net.Uri
import android.util.Size

data class VideoBlock(
    val content: Uri,
    val title: String?,
    val providerName: String?,
    val author: String?,
    val authorUrl: Uri?,
    val description: String?,
    val thumbnailUrl: Uri?,
    val thumbnailSize: Size?,
    val html: String?
): MediaBlock