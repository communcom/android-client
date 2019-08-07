package io.golos.cyber_android.core.keys_backup.facade

interface BackupKeysFacadeSync {
    fun getRawData(): ByteArray

    fun saveRawData(data: ByteArray)
}