package io.golos.cyber_android.backup

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.utils.OnDevicePersister
import io.golos.domain.Logger
import io.golos.domain.Persister
import io.golos.domain.entities.AuthState
import io.golos.sharedmodel.CyberName
import java.io.*

private const val BACKUP_KEYS = "keys_0"

class CommunBackupAgent: BackupAgent() {

    private val logger = object : Logger {
        override fun invoke(e: Exception) {

        }
    }

    override fun onRestore(data: BackupDataInput, appVersionCode: Int, newState: ParcelFileDescriptor) {
        println("onRestore 1")
        val persister: Persister = OnDevicePersister(this, logger)
        with(data) {
            while (readNextHeader()) {
                when (key) {
                    BACKUP_KEYS -> {
                        val dataBuf = ByteArray(dataSize).also {
                            readEntityData(it, 0, dataSize)
                        }
                        ByteArrayInputStream(dataBuf).also {
                            DataInputStream(it).apply {
                                val activeUser = readUTF()
                                val activeKey = readUTF()
                                persister.saveActiveKey(activeKey)
                                persister.saveAuthState(AuthState(activeUser.toCyberName(), true))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackup(oldState: ParcelFileDescriptor?, data: BackupDataOutput, newState: ParcelFileDescriptor) {
        println("onBackup 1")
        val persister: Persister = OnDevicePersister(this, logger)
        val activeKey = persister.getActiveKey()
        val authState = persister.getAuthState()
        if (authState?.isUserLoggedIn == true) {
            val activeUser = authState.user

            //saving state to local storage
            FileOutputStream(newState.fileDescriptor).run {
                writeKeysTo(this, activeUser, activeKey)
            }

            val buffer = ByteArrayOutputStream().run {
                writeKeysTo(this, activeUser, activeKey)
                toByteArray()
            }
            val len: Int = buffer.size
            data.apply {
                writeEntityHeader(BACKUP_KEYS, len)
                writeEntityData(buffer, len)
                println("onBackup 2")
            }

        }
    }

    private fun writeKeysTo(it: OutputStream, activeUser: CyberName, activeKey: String?) {
        DataOutputStream(it).apply {
            writeUTF(activeUser.name)
            writeUTF(activeKey)
        }
    }

}