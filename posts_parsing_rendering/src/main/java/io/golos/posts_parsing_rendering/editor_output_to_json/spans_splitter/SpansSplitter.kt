package io.golos.posts_parsing_rendering.editor_output_to_json.spans_splitter

import io.golos.domain.post.editor_output.LinkSpanInfo
import io.golos.domain.post.editor_output.MentionSpanInfo
import io.golos.domain.post.editor_output.ParagraphMetadata
import io.golos.domain.post.editor_output.TagSpanInfo
import java.lang.UnsupportedOperationException

class SpansSplitter {
    fun split(metadata: ParagraphMetadata): List<SplittedItem> {
        if(metadata.plainText.isEmpty()) {
            return listOf()
        }

        if(metadata.spans.isEmpty()) {
            return listOf(SplittedText(metadata.plainText))
        }

        val orderedSpans = metadata.spans.sortedBy { it.area.first }

        val result = mutableListOf<SplittedItem>()

        var firstTextIndex = 0
        var lastTextIndex = 0

        orderedSpans.forEach { span ->
            lastTextIndex = span.area.first

            if(firstTextIndex < lastTextIndex) {
                result.add(SplittedText(metadata.plainText.substring(firstTextIndex until lastTextIndex)))
            }

            val spanToAdd = when(span) {
                is TagSpanInfo -> SplittedTag(span.value)
                is MentionSpanInfo -> SplittedMention(span.value)
                is LinkSpanInfo -> SplittedLink(span.value.text, span.value.uri)
                else -> throw UnsupportedOperationException("This type of span is not supporred: ${span::class}")
            }

            result.add(spanToAdd)

            firstTextIndex = span.area.last
        }

        if(firstTextIndex <= metadata.plainText.lastIndex) {
            result.add(SplittedText(metadata.plainText.substring(firstTextIndex)))
        }

        return result
    }
}