package io.golos.posts_editor.components.input.text_tasks.dto

sealed class TextSlice(val text: String, val range: IntRange) {
    class MentionTextSlice (
        text: String,
        range: IntRange
    ) : TextSlice(text, range)

    class LinkTextSlice (
        text: String,
        range: IntRange
    ) : TextSlice(text, range)

    class TagSlice (
        text: String,
        range: IntRange
    ) : TextSlice(text, range)
}
