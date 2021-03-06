package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import android.net.Uri

data class RichBlock(
    val id: Long?,
    val content: Uri,
    val title: String?,
    val url: Uri?,
    val author: String?,
    val authorUrl: Uri?,
    val providerName: String?,
    val description: String?,
    val thumbnailUrl: Uri?,
    val thumbnailWidth: Int?,
    val thumbnailHeight: Int?,
    val html: String?
): MediaBlock