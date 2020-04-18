package io.golos.posts_editor.components.input.text_tasks_runner

import io.golos.posts_editor.components.input.text_tasks.TextTask
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class TextTasksRunner(private val tasks: List<TextTask>) : CoroutineScope {
    private var processingJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    /**
     * Starts search
     */
    fun runDelay(processedText: CharSequence?) {
        processingJob?.cancel()

        processingJob = launch {
            delay(500)

            Timber.tag("TEXT_TASKS").d("Run delay - start")

            try {
                tasks.forEach { task ->
                    withContext(Dispatchers.Default) {
                        task.process(processedText)
                    }
                }
            }
            catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    fun run(processedText: CharSequence?) {
        try {
            tasks.forEach { task ->
                task.process(processedText)
            }
        }
        catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    /**
     * Closes search engine
     */
        fun close() {
        processingJob?.cancel()
    }
}