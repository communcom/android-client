package io.golos.cyber_android.ui.screens.post_edit.dto

import android.net.Uri

data class ExternalLinkInfo (
 val type: ExternalLinkType,
 val description: String,
 val thumbnailUrl: Uri,
 val sourceUrl: Uri
)