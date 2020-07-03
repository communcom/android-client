package io.golos.utils.dates_local_now_calculator

import io.golos.utils.helpers.splitToCharSequences
import org.junit.Assert
import org.junit.Test

class SplitToCharSequencesTest {
    @Test
    fun emptySource() {
        // Arrange
        val source = ""

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun emptyDelimiter() {
        // Arrange
        val source = "some text"

        // Act
        val result = source.splitToCharSequences("")

        // Assert
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun withoutDelimiters() {
        // Arrange
        val source = "some text"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("some text", result[0])
    }

    @Test
    fun oneDelimiterOnly() {
        // Arrange
        val source = "|"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun severalDelimitersOnly() {
        // Arrange
        val source = "||"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun delimitersInTheStart() {
        // Arrange
        val source = "|some text"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("some text", result[0])
    }

    @Test
    fun delimitersInTheMiddle() {
        // Arrange
        val source = "some | text"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("some ", result[0])
        Assert.assertEquals(" text", result[1])
    }

    @Test
    fun delimitersInTheEnd() {
        // Arrange
        val source = "some text|"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("some text", result[0])
    }

    @Test
    fun setOfDelimitersInTheRow() {
        // Arrange
        val source = "||some||text||"

        // Act
        val result = source.splitToCharSequences("|")

        // Assert
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("some", result[0])
        Assert.assertEquals("text", result[1])
    }
}