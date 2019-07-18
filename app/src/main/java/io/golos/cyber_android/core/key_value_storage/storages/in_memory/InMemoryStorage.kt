package io.golos.cyber_android.core.key_value_storage.storages.in_memory

import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.key_value_storage.storages.StorageBase
import io.golos.cyber_android.core.key_value_storage.storages.StorageCommitOperations
import io.golos.cyber_android.core.key_value_storage.storages.StorageReadOperations
import java.util.*
import javax.inject.Inject

/** Storage based on in-memory dictionary */
@ApplicationScope
class InMemoryStorage
@Inject
constructor(): StorageBase() {
    private val storage: MutableMap<String, Any> = TreeMap()

    /** Create proxy for read */
    override fun createReadOperationsInstance(): StorageReadOperations = InMemoryStorageOperations(storage)

    /** Create proxy for read */
    override fun createWriteOperationsInstance(): StorageCommitOperations = InMemoryStorageOperations(storage)
}