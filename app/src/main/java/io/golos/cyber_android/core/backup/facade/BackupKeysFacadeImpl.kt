package io.golos.cyber_android.core.backup.facade

import android.app.backup.BackupManager
import android.content.Context
import io.golos.cyber_android.core.backup.raw_data.BackupKeysDataWrapper
import io.golos.cyber_android.core.backup.storage.BackupKeysDataStorage
import io.golos.domain.Encryptor
import javax.inject.Inject

class BackupKeysFacadeImpl
@Inject
constructor(
    private val context: Context,
    private val encryptor: Encryptor,
    private val backupManager: BackupManager
) : BackupKeysFacadeSync, BackupKeysFacade {

    override fun getRawData(): ByteArray = BackupKeysDataStorage(context, encryptor).getKeys().toBytes()

    override fun saveRawData(data: ByteArray) =
        BackupKeysDataWrapper(data).let { BackupKeysDataStorage(context, encryptor).saveKeys(it) }

    override fun putKey(userName: String, masterKey: String) {
        val storage = BackupKeysDataStorage(context, encryptor)

        val keys = storage.getKeys()

        val key = keys.getMasterKey(userName)

        if(key != null && key ==  masterKey) {
            return              // Nothing to update
        }

        keys.putMasterKey(userName, masterKey)

        storage.saveKeys(keys)

        backupManager.dataChanged()       // Tell backup to upload
    }

    override fun getKey(userName: String): String? = BackupKeysDataStorage(context, encryptor).getKeys().getMasterKey(userName)
}