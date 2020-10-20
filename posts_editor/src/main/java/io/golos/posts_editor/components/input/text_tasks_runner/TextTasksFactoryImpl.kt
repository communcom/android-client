package io.golos.posts_editor.components.input.text_tasks_runner

import io.golos.posts_editor.components.input.text_tasks.LinksTask
import io.golos.posts_editor.components.input.text_tasks.MentionsTask
import io.golos.posts_editor.components.input.text_tasks.TagsTask
import io.golos.posts_editor.components.input.text_tasks.TextTask

class TextTasksFactoryImpl : TextTasksFactory {
    override fun createTasks(): List<TextTask> = listOf(MentionsTask(), LinksTask(), TagsTask())
}