package io.golos.cyber_android.core.backup.agent

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.serviceLocator
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.UserKeyType
import io.golos.sharedmodel.CyberName
import java.io.*

@Suppress("unused")
class CommunBackupAgent: BackupAgent() {

    private val BACKUP_KEY = "KEYS_GEN_0"

    private val backupKeysFacade by lazy { serviceLocator.backupKeysFacadeSync }

    override fun onRestore(data: BackupDataInput, appVersionCode: Int, newState: ParcelFileDescriptor) {
        with(data) {
            while (readNextHeader()) {
                if(key == BACKUP_KEY) {
                    val dataBuf = readSourceData(data)

                    ByteArrayInputStream(dataBuf).also {
                        DataInputStream(it).apply {
                            backupKeysFacade.saveRawData(it.readBytes())
                        }
                    }
                }
            }
        }
    }

    override fun onBackup(oldState: ParcelFileDescriptor?, data: BackupDataOutput, newState: ParcelFileDescriptor) {
        val rawDataToBackup = backupKeysFacade.getRawData()

        //saving state to local storage
        FileOutputStream(newState.fileDescriptor).apply {
            write(rawDataToBackup)
        }

        //saving state to [data] which will be backed up
        data.apply {
            writeEntityHeader(BACKUP_KEY, rawDataToBackup.size)
            writeEntityData(rawDataToBackup, rawDataToBackup.size)
        }
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