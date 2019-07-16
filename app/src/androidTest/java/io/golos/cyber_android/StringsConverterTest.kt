package io.golos.cyber_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.golos.domain.StringsConverter
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringsConverterTest {

    companion object {
        private lateinit var converter: StringsConverter

        @BeforeClass
        @JvmStatic
        fun setUp() {
            converter = StringsConverterImpl()
        }
    }

    @Test
    fun toBytes() {
        // Arrange
        val source = "In a hole in the ground there lived a hobbit."

        // Act
        val testResult = converter.toBytes(source)
            .let { converter.fromBytes(it) }

        // Assert
        assertEquals(source, testResult)
    }

    @Test
    fun toBytesEmpty() {
        // Arrange
        val source = ""

        // Act
        val testResult = converter.toBytes(source)
            .let { converter.fromBytes(it) }

        // Assert
        assertEquals(source, testResult)
    }

    @Test
    fun toBase64() {
        // Arrange
        val source = "In a hole in the ground there lived a hobbit."

        // Act
        val testResult = converter.toBytes(source)
            .let { converter.toBase64(it) }
            .let { converter.fromBase64(it) }
            .let { converter.fromBytes(it) }

        // Assert
        assertEquals(source, testResult)
    }

    @Test
    fun toBase64Empty() {
        // Arrange
        val source = ""

        // Act
        val testResult = converter.toBytes(source)
            .let { converter.toBase64(it) }
            .let { converter.fromBase64(it) }
            .let { converter.fromBytes(it) }

        // Assert
        assertEquals(source, testResult)
    }
}