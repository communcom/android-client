package io.golos.cyber_android.core.backup.facade

interface BackupKeysFacadeSync {
    fun getRawData(): ByteArray

    fun saveRawData(data: ByteArray)
}