package io.golos.cyber_android.core.key_value_storage.storages

/** Interface for commit operations of a storage */
interface StorageCommitOperations: StorageWriteOperations {
    /** Complete editing  */
    fun commit()
}