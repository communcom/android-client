package io.golos.posts_editor.dto

enum class EditorAction {
    TEXT_BOLD,
    TEXT_ITALIC,
    TEXT_COLOR,

    TAG,
    MENTION,
    LINK,

    LOCAL_IMAGE,
    EXTERNAL_LINK
}