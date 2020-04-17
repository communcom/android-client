package io.golos.posts_editor.components.input.text_tasks

interface TextTask {
    fun process(oldText: CharSequence?): CharSequence?
}