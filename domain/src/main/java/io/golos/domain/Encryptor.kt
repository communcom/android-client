package io.golos.domain

interface Encryptor {
    fun encrypt(data: ByteArray?): ByteArray?

    fun decrypt(data: ByteArray?): ByteArray?
}