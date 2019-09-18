package io.golos.posts_editor.models

class Node {
    var type: EditorType? = null
    var content: MutableList<String>? = null

    var textSettings: TextSettings? = null
    var editorTextStyles: MutableList<EditorTextStyle> = mutableListOf()

    var childs: MutableList<Node>? = null
}
