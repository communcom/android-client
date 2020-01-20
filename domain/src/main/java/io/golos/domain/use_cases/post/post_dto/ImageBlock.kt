package io.golos.domain.use_cases.post.post_dto

import android.net.Uri

data class ImageBlock(
    val id: Long?,
    val content: Uri,
    val description: String?,
    val width: Int?,
    val height: Int?
): MediaBlock