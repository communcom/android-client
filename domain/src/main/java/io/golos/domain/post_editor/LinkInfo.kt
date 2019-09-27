package io.golos.domain.post_editor

import android.net.Uri

data class LinkInfo(
    val text: String,
    val type: LinkType,
    val uri: Uri,
    val thumbnailUri: Uri
)