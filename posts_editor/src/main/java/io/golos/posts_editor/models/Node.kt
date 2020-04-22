package io.golos.posts_editor.models

import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle

class Node {
    var type: EditorType? = null
    var content: MutableList<String>? = null

    var childs: MutableList<Node>? = null
}
