package io.golos.posts_editor.components.input.text_tasks_runner

import io.golos.posts_editor.components.input.text_tasks.TextTask

interface TasksFactory {
    fun createTasks(): List<TextTask>
}