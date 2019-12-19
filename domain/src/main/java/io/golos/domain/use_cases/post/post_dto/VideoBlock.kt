package io.golos.domain.use_cases.post.post_dto

import android.net.Uri
import android.util.Size

data class VideoBlock(
    val id: String?,
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