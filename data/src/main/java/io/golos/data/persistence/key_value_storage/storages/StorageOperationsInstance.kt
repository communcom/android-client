package io.golos.data.persistence.key_value_storage.storages

interface StorageOperationsInstance {
    /** Create proxy for read */
    fun createReadOperationsInstance(): StorageReadOperations

    /** Create proxy for read */
    fun createWriteOperationsInstance(): StorageCommitOperations
}