package io.golos.cyber_android.core.keys_backup.facade

interface BackupKeysFacade {
    fun putKey(userName: String, masterKey: String)

    fun getKey(userName: String): String?

    fun isStorageExists(): Boolean
}