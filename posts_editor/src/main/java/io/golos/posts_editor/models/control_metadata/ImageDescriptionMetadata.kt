package io.golos.posts_editor.models.control_metadata

import io.golos.posts_editor.models.EditorTextStyle
import io.golos.posts_editor.models.EditorType
import io.golos.posts_editor.models.TextSettings

/**
 * Metadata for an image description
 */
data class ImageDescriptionMetadata (
    override val type: EditorType = EditorType.IMG_SUB,
    var textSettings: TextSettings? = null,
    var editorTextStyles: MutableList<EditorTextStyle> = mutableListOf()
) : ControlMetadata