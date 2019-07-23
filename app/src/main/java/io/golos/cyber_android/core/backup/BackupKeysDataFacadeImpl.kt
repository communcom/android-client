package io.golos.cyber_android.core.backup

import java.lang.UnsupportedOperationException
import javax.inject.Inject

class BackupKeysDataFacadeImpl
constructor(private val sourceData: ByteArray) {
    fun toBytes(): ByteArray {
        throw UnsupportedOperationException()
    }

    fun putMasterKey(userName: String, masterKey: String) {

    }

    fun getMasterKey(userName: String): String? {
        return null
    }
}