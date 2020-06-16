package io.golos.posts_editor.components.input.text_tasks

import android.text.style.CharacterStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.spans.custom.TagSpan
import io.golos.domain.posts_parsing_rendering.post_metadata.spans_worker.SpansWorkerImpl
import io.golos.posts_editor.components.input.text_tasks.dto.TextSlice
import io.golos.posts_editor.utilities.post.spans.PostSpansFactory
import java.util.regex.Pattern

/**
 * Finds and marks all hashtags
 */
class TagsTask: TextTaskBase() {
    private val tagPattern = Pattern.compile("""#\w+""")

    override fun findSlices(sourceText: CharSequence): List<TextSlice> {
        val result = mutableListOf<TextSlice>()

        val tagsMatcher = tagPattern.matcher(sourceText)

        while (tagsMatcher.find()) {
            result.add(TextSlice.TagSlice(tagsMatcher.group(), tagsMatcher.start()..tagsMatcher.end()))
        }

        return result
    }

    override fun getAllSpans(spansWorker: SpansWorkerImpl): List<CharacterStyle> =
        spansWorker.getAllSpans<TagSpan>(TagSpan::class)

    override fun createSpan(text: String): CharacterStyle = PostSpansFactory.createTag(text)
}