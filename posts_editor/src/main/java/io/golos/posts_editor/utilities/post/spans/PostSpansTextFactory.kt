package io.golos.posts_editor.utilities.post.spans

object PostSpansTextFactory {
    fun createTagText(text: String) = "#$text"

    fun createMentionText(text: String) = "@$text"
}