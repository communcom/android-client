package io.golos.cyber_android.core.key_value_storage.storages.combined

import io.golos.cyber_android.core.key_value_storage.storages.StorageBase
import io.golos.cyber_android.core.key_value_storage.storages.StorageCommitOperations
import io.golos.cyber_android.core.key_value_storage.storages.StorageOperationsInstance
import io.golos.cyber_android.core.key_value_storage.storages.StorageReadOperations
import java.util.concurrent.locks.ReentrantReadWriteLock

/** Storage based on shared preferences and in-memory structure for cashing */
class CombinedStorage
constructor(
    private val cacheStorage: StorageOperationsInstance,
    private val persistentStorage: StorageOperationsInstance
): StorageBase() {

    private val lock = ReentrantReadWriteLock()

    /** Create proxy for read */
    override fun createReadOperationsInstance(): StorageReadOperations =
        CombinedStorageReadOperations(
            lock,
            persistentStorage.createReadOperationsInstance(),
            cacheStorage.createReadOperationsInstance(),
            cacheStorage.createWriteOperationsInstance())

    /** Create proxy for read */
    override fun createWriteOperationsInstance(): StorageCommitOperations =
        CombinedStorageUpdateOperations(
            lock,
            persistentStorage.createWriteOperationsInstance(),
            cacheStorage.createWriteOperationsInstance())
}