package io.golos.cyber_android.core.key_value_storage.storages.shared_preferences

import android.content.Context
import io.golos.cyber_android.core.key_value_storage.storages.StorageBase
import io.golos.cyber_android.core.key_value_storage.storages.StorageCommitOperations
import io.golos.cyber_android.core.key_value_storage.storages.StorageReadOperations

/** Storage based on shared preferences */
class SharedPreferencesStorage
constructor(
    private val appContext: Context
) : StorageBase() {

    private val storageName
        get() = "${appContext.packageName}.App"

    /** Create proxy for read */
    override fun createReadOperationsInstance(): StorageReadOperations =
        SharedPreferencesStorageReadOperations(appContext, storageName)

    /** Create proxy for read */
    override fun createWriteOperationsInstance(): StorageCommitOperations =
        SharedPreferencesStorageUpdateOperations(appContext, storageName)
}