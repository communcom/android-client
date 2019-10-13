package io.golos.cyber_android.core.keys_backup.storage

import android.content.Context
import io.golos.cyber_android.core.keys_backup.raw_data.BackupKeysDataWrapper
import io.golos.domain.Encryptor
import timber.log.Timber
import java.io.File

/**
 * File storage for backup data
 */
class BackupKeysDataStorage(private val context: Context, private val encryptor: Encryptor) {

    fun saveKeys(data: BackupKeysDataWrapper): Unit {
        try {
            data.toBytes()
                .let { encryptor.encrypt(it) }
                ?.let { getFile().writeBytes(it) }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun getKeys(): BackupKeysDataWrapper =
        try {
            val keysFile = getFile()

            if(!keysFile.exists()) {
                BackupKeysDataWrapper()
            } else {
                keysFile.readBytes()
                    .let { encryptor.decrypt(it) }
                    ?.let { BackupKeysDataWrapper(it) } ?: BackupKeysDataWrapper()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            BackupKeysDataWrapper()
        }

    fun isStorageExists(): Boolean = getFile().exists()

    private fun getFile(): File = File(context.filesDir, "keys.bak")
}