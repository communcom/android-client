package io.golos.cyber_android.backup

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.Build
import android.os.ParcelFileDescriptor
import com.squareup.moshi.Moshi
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.entities.AuthState
import io.golos.sharedmodel.CyberName
import java.io.*

private const val BACKUP_KEYS = "keys_0"

@Suppress("unused")
class CommunBackupAgent: BackupAgent() {

    private val keyValueStorage: KeyValueStorageFacade by lazy {
        KeyValueStorageFacadeImpl(SharedPreferencesStorage(this), Moshi.Builder().build())
    }

    private val stringsConverter: StringsConverter by lazy { StringsConverterImpl() }

    private val encryptor: Encryptor by lazy {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptorAES()
        } else  {
            EncryptorAESOldApi(keyValueStorage, EncryptorRSA(this))
        }
    }

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
        val activeKey = keyValueStorage.getActiveKey()!!
            .let { encryptor.decrypt(it)!! }
            .let {stringsConverter.fromBytes(it)}

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

        val activeKeyToStore = activeKey.let {stringsConverter.toBytes(it)}.let {encryptor.encrypt(it)!!}

        keyValueStorage.saveActiveKey(activeKeyToStore)
        keyValueStorage.saveAuthState(AuthState(activeUser.toCyberName(), true))
    }

}