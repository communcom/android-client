package io.golos.cyber_android.core.keys_backup.facade

import android.app.backup.BackupManager
import android.content.Context
import android.util.Log
import io.golos.cyber_android.core.keys_backup.raw_data.BackupKeysDataWrapper
import io.golos.cyber_android.core.keys_backup.storage.BackupKeysDataStorage
import io.golos.domain.Encryptor
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Inject
import javax.inject.Named

class BackupKeysFacadeImpl
@Inject
constructor(
    private val context: Context,
    @Named(Clarification.AES) private val encryptor: Encryptor,
    private val backupManager: BackupManager
) : BackupKeysFacadeSync, BackupKeysFacade {

    override fun getRawData(): ByteArray = BackupKeysDataStorage(context, encryptor).getKeys().toBytes()

    override fun saveRawData(data: ByteArray) =
        BackupKeysDataWrapper(data).let { BackupKeysDataStorage(context, encryptor).saveKeys(it) }

    override fun putKey(userName: String, masterKey: String) {
        Log.d("BACKUP_RESTORE", "BackupKeysFacadeImpl::putKey(). UserName: $userName; MasterKey: $masterKey")

        val storage = BackupKeysDataStorage(context, encryptor)

        val keys = storage.getKeys()

        val key = keys.getMasterKey(userName)

        if(key != null && key ==  masterKey) {
            return              // Nothing to update
        }

        keys.putMasterKey(userName, masterKey)

        storage.saveKeys(keys)

        Log.d("BACKUP_RESTORE", "BackupKeysFacadeImpl::putKey(). Tell backup service to upload")
        backupManager.dataChanged()       // Tell backup to upload
    }

    override fun getKey(userName: String): String? = BackupKeysDataStorage(context, encryptor).getKeys().getMasterKey(userName)

    override fun isStorageExists(): Boolean = BackupKeysDataStorage(context, encryptor).isStorageExists()
}