package io.golos.domain.post.editor_output

import android.net.Uri

data class EmbedMetadata (
    val type: EmbedType,
    val sourceUri: Uri,
    val displayUri: Uri,
    val description: String?
) : ControlMetadata
