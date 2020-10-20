package io.golos.cyber_android.ui.shared.widgets.comment

import io.golos.posts_editor.components.input.text_tasks.LinksTask
import io.golos.posts_editor.components.input.text_tasks.MentionsTask
import io.golos.posts_editor.components.input.text_tasks.TagsTask
import io.golos.posts_editor.components.input.text_tasks.TextTask
import io.golos.posts_editor.components.input.text_tasks_runner.TextTasksFactory

class CommentTextTasksFactoryImpl : TextTasksFactory {
    override fun createTasks(): List<TextTask> = listOf(MentionsTask(), LinksTask(), TagsTask())
}