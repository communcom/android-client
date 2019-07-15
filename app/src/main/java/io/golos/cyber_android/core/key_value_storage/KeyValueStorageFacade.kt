package io.golos.cyber_android.core.key_value_storage

interface KeyValueStorageFacade {

    fun setAESCryptoKey(key: ByteArray)


    fun getAESCryptoKey(): ByteArray?
}