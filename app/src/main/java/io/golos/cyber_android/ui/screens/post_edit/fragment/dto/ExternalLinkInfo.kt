package io.golos.cyber_android.ui.screens.post_edit.fragment.dto

import android.net.Uri

data class ExternalLinkInfo (
 val type: ExternalLinkType,
 val description: String,
 val thumbnailUrl: Uri,
 val sourceUrl: Uri
)