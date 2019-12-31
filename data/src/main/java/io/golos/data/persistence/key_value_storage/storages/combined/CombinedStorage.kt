package io.golos.data.persistence.key_value_storage.storages.combined

import io.golos.data.persistence.key_value_storage.storages.StorageBase
import io.golos.data.persistence.key_value_storage.storages.StorageCommitOperations
import io.golos.data.persistence.key_value_storage.storages.StorageOperationsInstance
import io.golos.data.persistence.key_value_storage.storages.StorageReadOperations
import io.golos.domain.dependency_injection.Clarification
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.inject.Inject
import javax.inject.Named

/** Storage based on shared preferences and in-memory structure for cashing */
class CombinedStorage
@Inject
constructor(
    @Named(Clarification.CACHE) private val cacheStorage: StorageOperationsInstance,
    @Named(Clarification.PERSISTENT) private val persistentStorage: StorageOperationsInstance
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