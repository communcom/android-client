package io.golos.posts_editor.models.control_metadata

import io.golos.posts_editor.models.EditorType

/**
 * Metadata for text paragraph
 */
data class InputMetadata (
        override val type: EditorType = EditorType.INPUT
) : ControlMetadata