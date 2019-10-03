package io.golos.posts_parsing_rendering

import io.golos.posts_parsing_rendering.mappers.comment_to_json.tags.CommentTagsExtractor
import org.junit.Test
import org.junit.Assert.*

class CommentTagsExtractorTest {
    @Test
    fun empty() {
        // Arrange
        val text = ""

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(1, splitResult.size)
        assertFalse(splitResult[0].isTag)
        assertEquals("", splitResult[0].text)
    }

    @Test
    fun withoutTags() {
        // Arrange
        val text = "test text"

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(1, splitResult.size)
        assertFalse(splitResult[0].isTag)
        assertEquals("test text", splitResult[0].text)
    }

    @Test
    fun startWithHash() {
        // Arrange
        val text = "#test text"

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(2, splitResult.size)

        assertTrue(splitResult[0].isTag)
        assertEquals("test", splitResult[0].text)

        assertFalse(splitResult[1].isTag)
        assertEquals(" text", splitResult[1].text)
    }

    @Test
    fun endWithHash() {
        // Arrange
        val text = "test text#"

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(1, splitResult.size)

        assertFalse(splitResult[0].isTag)
        assertEquals("test text", splitResult[0].text)
    }

    @Test
    fun oneHash() {
        // Arrange
        val text = "test #text"

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(2, splitResult.size)

        assertFalse(splitResult[0].isTag)
        assertEquals("test ", splitResult[0].text)

        assertTrue(splitResult[1].isTag)
        assertEquals("text", splitResult[1].text)
    }

    @Test
    fun twoHashes() {
        // Arrange
        val text = "test #text hi everybody #hello"

        // Act
        val splitResult = CommentTagsExtractor.extract(text)

        // Assert
        assertEquals(4, splitResult.size)

        assertFalse(splitResult[0].isTag)
        assertEquals("test ", splitResult[0].text)

        assertTrue(splitResult[1].isTag)
        assertEquals("text", splitResult[1].text)

        assertFalse(splitResult[2].isTag)
        assertEquals(" hi everybody ", splitResult[2].text)

        assertTrue(splitResult[3].isTag)
        assertEquals("hello", splitResult[3].text)
    }
}