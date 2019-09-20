package io.golos.cyber_android.ui.shared_fragments.editor.dto

data class ExternalLinkInfo (
 val type: ExternalLinkType,
 val description: String?,
 val title: String?,
 val thumbnailUrl: String?,
 val sourceUrl: String
)