package io.golos.cyber_android.core.backup.agent

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.serviceLocator
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.UserKeyType
import io.golos.sharedmodel.CyberName
import java.io.*

private const val BACKUP_KEYS = "keys_0"

@Suppress("unused")
class CommunBackupAgent: BackupAgent() {

    private val keyValueStorage: KeyValueStorageFacade by lazy {
        serviceLocator.keyValueStorage
    }

    private val stringsConverter: StringsConverter by lazy { serviceLocator.stringsConverter }

    private val encryptor: Encryptor by lazy { serviceLocator.encryptor }

    private val userKeyStore by lazy { serviceLocator.userKeyStore }

    override fun onRestore(data: BackupDataInput, appVersionCode: Int, newState: ParcelFileDescriptor) {
        with(data) {
            while (readNextHeader()) {
                when (key) {
                    BACKUP_KEYS -> {
                        val dataBuf = ByteArray(dataSize).also {
                            readEntityData(it, 0, dataSize)
                        }
                        ByteArrayInputStream(dataBuf).also {
                            DataInputStream(it).apply {
                                readKeysTo()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackup(oldState: ParcelFileDescriptor?, data: BackupDataOutput, newState: ParcelFileDescriptor) {
        val activeKey = userKeyStore.getKey(UserKeyType.ACTIVE)

        val authState = keyValueStorage.getAuthState()

        if (authState?.isUserLoggedIn == true) {
            val activeUser = authState.user

            //saving state to local storage
            FileOutputStream(newState.fileDescriptor).run {
                writeKeys(activeUser, activeKey)
            }

            //saving state to [data] which will be backed up
            val buffer = ByteArrayOutputStream().run {
                writeKeys(activeUser, activeKey)
                toByteArray()
            }
            val len: Int = buffer.size
            data.apply {
                writeEntityHeader(BACKUP_KEYS, len)
                writeEntityData(buffer, len)
            }

        }
    }

    private fun OutputStream.writeKeys(activeUser: CyberName, activeKey: String?) {
        DataOutputStream(this).apply {
            writeUTF(activeUser.name)
            writeUTF(activeKey)
        }
    }

    private fun DataInputStream.readKeysTo() {
        val activeUser = readUTF()
        val activeKey = readUTF()

//        val activeKeyToStore = activeKey.let {stringsConverter.toBytes(it)}.let {encryptor.encrypt(it)!!}

        //keyValueStorage.saveUserKey(activeKeyToStore, UserKeyType.ACTIVE)  // todo Temporary - AS
        keyValueStorage.saveAuthState(AuthState(activeUser.toCyberName(), true, false, false, false))
    }

}