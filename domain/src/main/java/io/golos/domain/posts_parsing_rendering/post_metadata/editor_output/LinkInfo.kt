package io.golos.domain.posts_parsing_rendering.post_metadata.editor_output

import android.net.Uri

data class LinkInfo(
    val text: String,
    val uri: Uri
)