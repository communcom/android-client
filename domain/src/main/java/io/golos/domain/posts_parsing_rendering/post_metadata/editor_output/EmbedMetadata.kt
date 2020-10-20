package io.golos.domain.posts_parsing_rendering.post_metadata.editor_output

import android.net.Uri

data class EmbedMetadata (
    val type: EmbedType,
    val sourceUri: Uri,
    val displayUri: Uri,
    val description: String?
) : ControlMetadata
