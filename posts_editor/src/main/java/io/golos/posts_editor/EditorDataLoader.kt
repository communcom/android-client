package io.golos.posts_editor

import android.net.Uri
import io.golos.domain.use_cases.post.editor_output.EmbedType

/**
 * Set of methods for uploading part of posts to the editor
 */
interface EditorDataLoader {
    fun insertParagraph(text: CharSequence)

    fun insertEmbed(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String? = null)
}