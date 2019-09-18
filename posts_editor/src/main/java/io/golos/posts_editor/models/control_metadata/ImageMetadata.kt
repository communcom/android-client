package io.golos.posts_editor.models.control_metadata

import io.golos.posts_editor.models.EditorType

data class ImageMetadata (
    override val type: EditorType,
    var path: String? = null,
    var cords: String? = null
) : ControlMetadata
