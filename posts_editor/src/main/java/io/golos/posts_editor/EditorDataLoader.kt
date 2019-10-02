package io.golos.posts_editor

import android.net.Uri
import androidx.annotation.ColorInt
import io.golos.domain.post.editor_output.EmbedType
import java.time.format.TextStyle

/**
 * Set of methods for uploading part of posts to the editor
 */
interface EditorDataLoader {
    fun insertParagraph(text: CharSequence)

    fun insertEmbed(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String? = null)
}