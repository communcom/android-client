package io.golos.posts_editor.components.input.text_tasks

import android.text.style.CharacterStyle
import io.golos.domain.validation.user_name.UserNameValidationResult
import io.golos.domain.validation.user_name.UserNameValidatorImpl
import io.golos.posts_editor.components.input.spans.custom.MentionSpan
import io.golos.posts_editor.components.input.spans.spans_worker.SpansWorkerImpl
import io.golos.posts_editor.components.input.text_tasks.dto.TextSlice
import io.golos.posts_editor.utilities.post.spans.PostSpansFactory
import java.util.regex.Pattern

/**
 * Finds and marks all mentions
 */
class MentionsTask: TextTask {
    companion object {
        private const val mentionDraft = """@[a-z0-9-.]+"""
        private const val mentionValidChar = """[a-z0-9-.]"""
    }

    private var priorMentions: List<TextSlice>? = null
    private var currentSpans: List<CharacterStyle>? = null

    private val userNameValidator = UserNameValidatorImpl()

    private val mentionPattern = Pattern.compile(mentionDraft)
    private val validCharacterPattern = Pattern.compile(mentionValidChar)

    override fun process(oldText: CharSequence?): CharSequence? =
        oldText?.let {
            val allMentions = findMentions(it)

            if(needUpdate(allMentions, priorMentions)) {
                removeMentions(it, currentSpans)
                currentSpans = insertMentions(it, allMentions)
                priorMentions = allMentions
            }

            it
        }

    private fun findMentions(sourceText: CharSequence): List<TextSlice> {
        val result = mutableListOf<TextSlice>()

        val mentionMatcher = mentionPattern.matcher(sourceText)

        while (mentionMatcher.find()) {
            val start = mentionMatcher.start()
            val end = mentionMatcher.end()
            val text = mentionMatcher.group()

            if(start == 0 || !validCharacterPattern.matcher(sourceText[start-1].toString()).find()) {
                val userNameExtracted = text.removeRange(0..0)
                if(userNameValidator.validate(userNameExtracted) == UserNameValidationResult.SUCCESS) {
                    result.add(
                        TextSlice(
                            text,
                            start..end
                        )
                    )
                }
            }
        }

        return result
    }

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

    private fun removeMentions(text: CharSequence, oldSpans: List<CharacterStyle>?) {
        val spansWorker = SpansWorkerImpl(text)

        val spansToRemove = oldSpans ?: spansWorker.getAllSpans<MentionSpan>(MentionSpan::class)

        spansToRemove.forEach {
            spansWorker.removeSpan(it)
        }
    }

    private fun insertMentions(text: CharSequence, mentions: List<TextSlice>) : List<CharacterStyle> {
        val result = mutableListOf<CharacterStyle>()

        if(mentions.isEmpty() || text.isEmpty()) {
            return result
        }

        val spansWorker = SpansWorkerImpl(text)

        mentions.forEach { mention ->
            val span = PostSpansFactory.createMention(mention.text)
            result.add(span)
            spansWorker.appendSpan(span, mention.range)
        }

        return result
    }
}