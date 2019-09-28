package io.golos.domain.post.post_dto

import android.net.Uri

data class WebsiteBlock(
    val content: Uri,
    val thumbnailUrl: Uri?,
    val title: String?,
    val providerName: String?,
    val description: String?
): MediaBlock