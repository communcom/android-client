package io.golos.cyber_android.core.keys_backup.agent

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import android.util.Log.d
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.AppComponent
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacadeSync
import io.golos.shared_core.MurmurHash
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.FileOutputStream
import javax.inject.Inject

@Suppress("unused")
class CommunBackupAgent: BackupAgent() {

    private val BACKUP_KEY = "KEYS_GEN_0"

    @Inject
    internal lateinit var backupKeysFacade: BackupKeysFacadeSync

    override fun onCreate() {
        super.onCreate()
        App.injections.get<AppComponent>().inject(this)
    }

    override fun onRestore(data: BackupDataInput, appVersionCode: Int, newState: ParcelFileDescriptor) {
        d("BACKUP_RESTORE", "CommunBackupAgent::onRestore(): started. Thread: ${Thread.currentThread().name}")

        with(data) {
            while (readNextHeader()) {
                if(key == BACKUP_KEY) {
                    val dataBuf = readSourceData(data)

                    ByteArrayInputStream(dataBuf).also {
                        DataInputStream(it).apply {
                            val rawDataToRestore = it.readBytes()
                            d("BACKUP_RESTORE", "CommunBackupAgent::onRestore(): data size: ${rawDataToRestore.size}; data hash: ${MurmurHash.hash64(rawDataToRestore)}")
                            backupKeysFacade.saveRawData(rawDataToRestore)
                        }
                    }
                }
            }
        }
        d("BACKUP_RESTORE", "CommunBackupAgent::onRestore(): completed")
    }

    override fun onBackup(oldState: ParcelFileDescriptor?, data: BackupDataOutput, newState: ParcelFileDescriptor) {
        d("BACKUP_RESTORE", "CommunBackupAgent::onBackup(): started. Thread: ${Thread.currentThread().name}")

        val rawDataToBackup = backupKeysFacade.getRawData()
        d("BACKUP_RESTORE", "CommunBackupAgent::onBackup(): data size: ${rawDataToBackup.size}; data hash: ${MurmurHash.hash64(rawDataToBackup)}")

        //saving state to local storage
        FileOutputStream(newState.fileDescriptor).apply {
            write(rawDataToBackup)
        }

        //saving state to [data] which will be backed up
        data.apply {
            writeEntityHeader(BACKUP_KEY, rawDataToBackup.size)
            writeEntityData(rawDataToBackup, rawDataToBackup.size)
        }

        d("BACKUP_RESTORE", "CommunBackupAgent::onBackup(): completed")
    }

    private fun readSourceData(data: BackupDataInput): ByteArray =
        ByteArray(data.dataSize).also { buffer ->
            var offset = 0

            var bytesRead = data.readEntityData(buffer, offset, data.dataSize-offset)

            if(bytesRead != data.dataSize) {
                offset += bytesRead

                bytesRead = data.readEntityData(buffer, offset, data.dataSize-offset)

                while (bytesRead != 0) {
                    offset += bytesRead
                    bytesRead = data.readEntityData(buffer, offset, data.dataSize-offset)
                }
            }
        }
}