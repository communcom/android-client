package io.golos.cyber_android.core.key_value_storage.storages

/** Interface for write operations of a storage */
interface StorageWriteOperations {
    /** Put boolean value  */
    fun putBoolean(key: String, value: Boolean)

    /** Put string value  */
    fun putString(key: String, value: String)

    /** Put float value  */
    fun putFloat(key: String, value: Float)

    /** Put int value  */
    fun putInt(key: String, value: Int)

    /** Put long value  */
    fun putLong(key: String, value: Long)

    /** Put byte[] value  */
    fun putBytes(key: String, value: ByteArray)

    /** Remove value by key  */
    fun remove(key: String)

    /** Remove all values  */
    fun removeAll()
}