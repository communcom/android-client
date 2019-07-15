package io.golos.cyber_android.encryption

import io.golos.cyber_android.core.encryption.Encryptor
import io.golos.cyber_android.core.strings_converter.StringsConverter
import org.junit.Assert
import org.junit.Test

abstract class EncryptorTestBase {
    abstract val converter: StringsConverter
    abstract val encryptionUtils: Encryptor

    @Test
    fun encryptDecrypt() {
        // Arrange
        val source = "In a hole in the ground there lived a hobbit."

        // Act
        val testResult = converter.toBytes(source)
            .let { encryptionUtils.encrypt(it) }
            .let { encryptionUtils.decrypt(it) }
            .let { converter.fromBytes(it!!) }

        // Assert
        Assert.assertEquals(source, testResult)
    }

    @Test
    fun encryptEmpty() {
        // Arrange
        val source = ByteArray(0)

        // Act
        val testResult = encryptionUtils.encrypt(source)

        // Arrange
        Assert.assertNotNull(testResult)
        Assert.assertEquals(source.size, testResult!!.size)
        Assert.assertEquals(0, testResult.size)
    }

    @Test
    fun decryptEmpty() {
        // Arrange
        val source = ByteArray(0)

        // Act
        val testResult = encryptionUtils.decrypt(source)

        // Assert
        Assert.assertNotNull(testResult)
        Assert.assertEquals(source.size, testResult!!.size)
        Assert.assertEquals(0, testResult.size)
    }

    @Test
    fun encryptNull() {
        // Arrange
        val source = null

        // Act
        val testResult = encryptionUtils.encrypt(source)

        // Arrange
        Assert.assertNull(testResult)
    }

    @Test
    fun decryptNull() {
        // Arrange
        val source = null

        // Act
        val testResult = encryptionUtils.decrypt(source)

        // Assert
        Assert.assertNull(testResult)
    }
}