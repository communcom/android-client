package io.golos.domain.use_cases.post.post_dto

import android.net.Uri

data class ImageBlock(
    val content: Uri,
    val description: String?
): MediaBlock