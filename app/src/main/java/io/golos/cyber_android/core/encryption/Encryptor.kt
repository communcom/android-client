package io.golos.cyber_android.core.encryption

interface Encryptor {
    fun encrypt(data: ByteArray?): ByteArray?

    fun decrypt(data: ByteArray?): ByteArray?
}