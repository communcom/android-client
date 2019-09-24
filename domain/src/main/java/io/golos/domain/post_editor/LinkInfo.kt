package io.golos.domain.post_editor

import android.net.Uri

data class LinkInfo(
    val type: LinkType,
    val uri: Uri
)