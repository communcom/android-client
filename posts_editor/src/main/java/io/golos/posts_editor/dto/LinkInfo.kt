package io.golos.posts_editor.dto

import android.net.Uri

data class LinkInfo(
    val type: LinkType,
    val uri: Uri
)