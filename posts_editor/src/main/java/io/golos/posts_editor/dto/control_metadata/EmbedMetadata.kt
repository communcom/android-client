package io.golos.posts_editor.dto.control_metadata

import android.net.Uri
import io.golos.posts_editor.dto.EmbedType

data class EmbedMetadata (
    val type: EmbedType,
    val sourceUri: Uri,
    val displayUri: Uri,
    val description: String?
) : ControlMetadata
