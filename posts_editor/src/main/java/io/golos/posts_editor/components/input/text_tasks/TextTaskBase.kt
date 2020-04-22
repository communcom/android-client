package io.golos.posts_editor.components.input.text_tasks

import android.text.style.CharacterStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker.SpansWorkerImpl
import io.golos.posts_editor.components.input.text_tasks.dto.TextSlice
import io.golos.posts_editor.components.input.text_tasks.dto.TextTaskResult

/**
 * Finds and marks all mentions
 */
abstract class TextTaskBase: TextTask {
    private var priorSlices: List<TextSlice>? = null
    private var currentSpans: List<CharacterStyle>? = null

    override fun process(oldText: CharSequence?): TextTaskResult =
        oldText?.let {
            val allSlices = findSlices(it)

            if(needUpdate(allSlices, priorSlices)) {
                removeOldSpans(it, currentSpans)
                currentSpans = insertSpans(it, allSlices)
                priorSlices = allSlices
            }
            TextTaskResult(allSlices)

        } ?: TextTaskResult(listOf())

    protected abstract fun findSlices(sourceText: CharSequence): List<TextSlice>

    protected abstract fun getAllSpans(spansWorker: SpansWorkerImpl): List<CharacterStyle>

    protected abstract fun createSpan(text: String): CharacterStyle

    private fun needUpdate(newMentions: List<TextSlice>, oldMentions: List<TextSlice>?): Boolean {
        if(oldMentions == null) {
            return true         // The very first time
        }

        if(newMentions.size != oldMentions.size) {
            return true
        }

        for(i in newMentions.indices) {
            if(newMentions[i] != oldMentions[i]) {
                return true
            }
        }

        return false
    }

    private fun removeOldSpans(text: CharSequence, oldSpans: List<CharacterStyle>?) {
        val spansWorker =
            SpansWorkerImpl(text)

        val spansToRemove = oldSpans ?: getAllSpans(spansWorker)

        spansToRemove.forEach {
            spansWorker.removeSpan(it)
        }
    }

    private fun insertSpans(text: CharSequence, mentions: List<TextSlice>) : List<CharacterStyle> {
        val result = mutableListOf<CharacterStyle>()

        if(mentions.isEmpty() || text.isEmpty()) {
            return result
        }

        val spansWorker =
            SpansWorkerImpl(text)

        mentions.forEach { mention ->
            val span = createSpan(mention.text)
            result.add(span)
            spansWorker.appendSpan(span, mention.range)
        }

        return result
    }
}