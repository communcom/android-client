package io.golos.domain.post.editor_output

import android.net.Uri
import io.golos.domain.post.LinkType

data class LinkInfo(
    val text: String,
    val type: LinkType,
    val uri: Uri,
    val thumbnailUri: Uri
)