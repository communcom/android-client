package io.golos.posts_parsing_rendering

import io.golos.domain.post.editor_output.MentionSpanInfo
import io.golos.domain.post.editor_output.ParagraphMetadata
import io.golos.domain.post.editor_output.TagSpanInfo
import io.golos.posts_parsing_rendering.mappers.editor_output_to_json.spans_splitter.*
import org.junit.Test

import org.junit.Assert.*

class SpansSplitterTest {
    @Test
    fun empty() {
        // Arrange
        val paragraphMetadata = ParagraphMetadata("", listOf())

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertTrue(splitResult.isEmpty())
    }

    @Test
    fun textOnly() {
        // Arrange
        val paragraphMetadata = ParagraphMetadata("Some text", listOf())

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertEquals(1, splitResult.size)
        assertTrue(splitResult[0] is SplittedText)
        assertEquals("Some text", (splitResult[0] as SplittedText).content)
    }

    @Test
    fun onlySpan() {
        // Arrange
        val paragraphMetadata =
            ParagraphMetadata("#tag", listOf(TagSpanInfo(0..4, "tag")))

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertEquals(1, splitResult.size)
        assertTrue(splitResult[0] is SplittedTag)
        assertEquals("tag", (splitResult[0] as SplittedTag).content)
    }

    @Test
    fun one() {
        // Arrange
        val paragraphMetadata = ParagraphMetadata(
            "ggg #tag gggg",
            listOf(TagSpanInfo(4..8, "tag"))
        )

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertEquals(3, splitResult.size)

        assertTrue(splitResult[0] is SplittedText)
        assertEquals("ggg ", (splitResult[0] as SplittedText).content)

        assertTrue(splitResult[1] is SplittedTag)
        assertEquals("tag", (splitResult[1] as SplittedTag).content)

        assertTrue(splitResult[2] is SplittedText)
        assertEquals(" gggg", (splitResult[2] as SplittedText).content)
    }

    @Test
    fun veryFirstAndLast() {
        // Arrange
        val paragraphMetadata = ParagraphMetadata(
            "#tag gggg#tag",
            listOf(
                TagSpanInfo(0..4, "tag"),
                TagSpanInfo(9..13, "tag")
            )
        )

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertEquals(3, splitResult.size)

        assertTrue(splitResult[0] is SplittedTag)
        assertEquals("tag", (splitResult[0] as SplittedTag).content)

        assertTrue(splitResult[1] is SplittedText)
        assertEquals(" gggg", (splitResult[1] as SplittedText).content)

        assertTrue(splitResult[2] is SplittedTag)
        assertEquals("tag", (splitResult[2] as SplittedTag).content)
    }

    @Test
    fun variousTypes() {
        // Arrange
        val paragraphMetadata = ParagraphMetadata(
            "#tag @mention",
            listOf(
                TagSpanInfo(0..4, "tag"),
                MentionSpanInfo(5..13, "mention")
            )
        )

        // Act
        val splitResult = SpansSplitter().split(paragraphMetadata)

        // Assert
        assertEquals(3, splitResult.size)

        assertTrue(splitResult[0] is SplittedTag)
        assertEquals("tag", (splitResult[0] as SplittedTag).content)

        assertTrue(splitResult[1] is SplittedText)
        assertEquals(" ", (splitResult[1] as SplittedText).content)

        assertTrue(splitResult[2] is SplittedMention)
        assertEquals("mention", (splitResult[2] as SplittedMention).content)
    }
}