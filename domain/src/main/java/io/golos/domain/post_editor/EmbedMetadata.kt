package io.golos.domain.post_editor

import android.net.Uri

data class EmbedMetadata (
    val type: EmbedType,
    val sourceUri: Uri,
    val displayUri: Uri,
    val description: String?
) : ControlMetadata
