package io.golos.cyber_android.core.keys_backup.raw_data

import io.golos.shared_core.MurmurHash

/**
 * Wrapper around raw backup data
 */
class BackupKeysDataWrapper
@JvmOverloads
constructor (sourceData: ByteArray = ByteArray(0)) {

    private var rawData = BackupKeysRawData.KeysList.parseFrom(sourceData)

    private val keysList
        get() = rawData.keyList

    fun toBytes(): ByteArray = rawData.toByteArray()

    fun putMasterKey(userName: String, masterKey: String) {
        val userNameCode = MurmurHash.hash64(userName)

        val oldKeyRecord = keysList.firstOrNull { it.userName == userNameCode }

        if(oldKeyRecord != null && oldKeyRecord.masterKey == masterKey) {
            return          // Without changes
        }

        rawData = BackupKeysRawData.KeysList.newBuilder()
            .apply {
                keysList.forEach { oldKey ->
                    BackupKeysRawData.KeyInfo.newBuilder()
                        .apply {
                            this.masterKey = if(oldKey.userName == userNameCode) masterKey else oldKey.masterKey
                            this.userName = oldKey.userName
                        }
                        .build()
                        .apply { addKey(this) }
                }

                if(oldKeyRecord == null) {
                    BackupKeysRawData.KeyInfo.newBuilder()
                        .setUserName(userNameCode)
                        .setMasterKey(masterKey)
                        .build()
                        .apply { addKey(this) }
                }
            }
            .build()
    }

    /**
     * @return null if user name is not found
     */
    fun getMasterKey(userName: String): String? =
        MurmurHash.hash64(userName)
            .let { userNameCode ->
                keysList.singleOrNull { it.userName == userNameCode }?.masterKey
            }
}