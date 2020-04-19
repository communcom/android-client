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
class MentionsTask: TextTaskBase() {
    companion object {
        private const val mentionDraft = """@[a-z0-9-.]+"""
        private const val mentionValidChar = """[a-z0-9-.]"""
    }

    private val userNameValidator = UserNameValidatorImpl()

    private val mentionPattern = Pattern.compile(mentionDraft)
    private val validCharacterPattern = Pattern.compile(mentionValidChar)

    override fun findSlices(sourceText: CharSequence): List<TextSlice> {
        val result = mutableListOf<TextSlice>()

        val mentionMatcher = mentionPattern.matcher(sourceText)

        while (mentionMatcher.find()) {
            val start = mentionMatcher.start()
            val end = mentionMatcher.end()
            val text = mentionMatcher.group()

            if(start == 0 || !validCharacterPattern.matcher(sourceText[start-1].toString()).find()) {
                val userNameExtracted = text.removeRange(0..0)
                if(userNameValidator.validate(userNameExtracted) == UserNameValidationResult.SUCCESS) {
                    result.add(TextSlice.MentionTextSlice(text, start..end))
                }
            }
        }

        return result
    }

    override fun getAllSpans(spansWorker: SpansWorkerImpl): List<CharacterStyle> =
        spansWorker.getAllSpans<MentionSpan>(MentionSpan::class)

    override fun createSpan(text: String): CharacterStyle = PostSpansFactory.createMention(text)
}