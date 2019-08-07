package io.golos.cyber_android.backup

import io.golos.cyber_android.core.keys_backup.raw_data.BackupKeysDataWrapper
import org.junit.Assert.*
import org.junit.Test


class BackupKeysDataWrapperTest {
    @Test
    fun create() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        val bytes = wrapper.toBytes()

        // Assert
        assertEquals(0, bytes.size)
    }

    @Test
    fun addToEmptyList() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        wrapper.putMasterKey("user1", "master_key_1")
        val key = wrapper.getMasterKey("user1")

        // Assert
        assertNotNull(key)
        assertEquals("master_key_1", key)
    }

    @Test
    fun addToNotEmptyList() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        wrapper.putMasterKey("user1", "master_key_1")
        wrapper.putMasterKey("user2", "master_key_2")

        val key1 = wrapper.getMasterKey("user1")
        val key2 = wrapper.getMasterKey("user2")

        // Assert
        assertNotNull(key1)
        assertEquals("master_key_1", key1)

        assertNotNull(key2)
        assertEquals("master_key_2", key2)
    }

    @Test
    fun update() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        wrapper.putMasterKey("user1", "master_key_1")
        wrapper.putMasterKey("user1", "master_key_2")

        val key = wrapper.getMasterKey("user1")

        // Assert
        assertNotNull(key)
        assertEquals("master_key_2", key)
    }

    @Test
    fun keyNotFoundEmpty() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        val key = wrapper.getMasterKey("user1")

        // Assert
        assertNull(key)
    }

    @Test
    fun keyNotFoundNotEmpty() {
        // Arrange
        val wrapper = BackupKeysDataWrapper()

        // Act
        wrapper.putMasterKey("user1", "master_key_1")

        val key = wrapper.getMasterKey("user2")

        // Assert
        assertNull(key)
    }

    @Test
    fun restore() {
        // Arrange
        val oldWrapper = BackupKeysDataWrapper()

        // Act
        oldWrapper.putMasterKey("user1", "master_key_1")

        val newWrapper = BackupKeysDataWrapper(oldWrapper.toBytes())

        val key = newWrapper.getMasterKey("user1")

        // Assert
        assertNotNull(key)
        assertEquals("master_key_1", key)
    }
}