package io.golos.cyber_android.core.key_value_storage.storages

import io.golos.cyber_android.application.App

/** Base class for storage */
abstract class StorageBase: Storage, StorageOperationsInstance {
    /** Read data from storage */
    override fun <T>read(readFunc: (StorageReadOperations) -> T): T = readFunc(createReadOperationsInstance())

    /** Update data in storage */
    override fun update(updateAction: (StorageWriteOperations) -> Unit) {
        try{
            val operationsInstance = createWriteOperationsInstance()
            updateAction(operationsInstance)
            operationsInstance.commit()
        } catch(ex: Exception) {
            App.logger.log(ex)
            throw ex
        }
    }

    /** Create proxy for read */
    abstract override fun createReadOperationsInstance(): StorageReadOperations

    /** Create proxy for read */
    abstract override fun createWriteOperationsInstance(): StorageCommitOperations
}