package io.golos.posts_editor.components.input.text_tasks_runner

import com.google.android.material.textfield.TextInputEditText
import io.golos.posts_editor.components.input.text_tasks.TextTask
import io.golos.posts_editor.components.input.text_tasks.dto.TextSlice
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class TextTasksRunner(private val tasksFactory: TasksFactory) : CoroutineScope {
    private var processingJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val priorTextLen = mutableMapOf<Int, Int>()
    private val tasks = mutableMapOf<Int, List<TextTask>>()

    private var onPasteListener: ((List<TextSlice>) -> Unit)? = null

    /**
     * Starts search
     */
    fun runDelay(processedText: CharSequence?, editor: TextInputEditText) {
        processingJob?.cancel()

        processingJob = launch {
            val key = editor.hashCode()

            delay(250)

            try {
                val tasks = getTasks(key)

                val allSlices = mutableListOf<TextSlice>()

                tasks.forEach { task ->
                    withContext(Dispatchers.Default) {
                        allSlices.addAll(task.process(processedText).slices)
                    }
                }

                val currentTextLen = processedText?.length ?:  0
                if(currentTextLen - getPriorTextLen(key) > 10) {
                    onPasteListener?.invoke(allSlices)
                }
                setPriorTextLen(key, currentTextLen)
            }
            catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    /**
     * Closes search engine
     */
    fun close() {
        processingJob?.cancel()
    }

    fun setOnPasteListener(listener: ((List<TextSlice>) -> Unit)?) {
        onPasteListener = listener
    }

    private fun getTasks(key: Int): List<TextTask> = tasks[key] ?: tasksFactory.createTasks().also { tasks[key] = it }

    private fun getPriorTextLen(key: Int): Int = priorTextLen[key] ?: 0

    private fun setPriorTextLen(key: Int, value: Int) {
        priorTextLen[key] = value
    }
}