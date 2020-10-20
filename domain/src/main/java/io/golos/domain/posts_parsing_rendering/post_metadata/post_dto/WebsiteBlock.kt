package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import android.net.Uri

data class WebsiteBlock(
    val id: Long?,
    val content: Uri,
    val thumbnailUrl: Uri?,
    val title: String?,
    val providerName: String?,
    val description: String?
): MediaBlock