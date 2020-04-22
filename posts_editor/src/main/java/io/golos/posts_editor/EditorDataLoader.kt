package io.golos.posts_editor

import android.net.Uri
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.EmbedType

/**
 * Set of methods for uploading part of posts to the editor
 */
interface EditorDataLoader {
    fun insertParagraph(text: CharSequence)

    fun insertEmbed(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String? = null)
}