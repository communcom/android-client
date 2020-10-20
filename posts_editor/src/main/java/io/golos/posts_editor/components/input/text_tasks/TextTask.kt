package io.golos.posts_editor.components.input.text_tasks

import io.golos.posts_editor.components.input.text_tasks.dto.TextTaskResult

interface TextTask {
    fun process(oldText: CharSequence?): TextTaskResult
}