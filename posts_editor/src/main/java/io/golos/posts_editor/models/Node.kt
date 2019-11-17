package io.golos.posts_editor.models

import io.golos.domain.use_cases.post.TextStyle

class Node {
    var type: EditorType? = null
    var content: MutableList<String>? = null

    var textSettings: TextSettings? = null
    var editorTextStyles: MutableList<TextStyle> = mutableListOf()

    var childs: MutableList<Node>? = null
}
