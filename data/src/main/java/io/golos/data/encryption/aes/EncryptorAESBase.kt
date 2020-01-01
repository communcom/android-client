package io.golos.data.encryption.aes

import io.golos.domain.Encryptor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec

/** Base class for AES encryption */
abstract class EncryptorAESBase : Encryptor, EncryptorFingerprint {
    protected companion object {
        @JvmStatic
        protected val CRYPTO_ALG = "AES/GCM/NoPadding"

        const val KEY_SIZE = 128                      // in bits
        private const val IV_SIZE = 12                          // in bytes
        private const val BLOCK_SIZE = 16                       // in bytes
    }

    protected val secureRandom = SecureRandom()

    override fun encrypt(data: ByteArray?): ByteArray? {
        if(data == null) {
            return null
        }

        if(data.isEmpty()) {
            return ByteArray(0)
        }

        // Get key
        val secretKey = getKey()

        // Generate initial vector (IV)
        val iv = ByteArray(IV_SIZE)
        secureRandom.nextBytes(iv)

        val parameterSpec = GCMParameterSpec(KEY_SIZE, iv)

        val input = Cipher.getInstance(CRYPTO_ALG)
        input.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        lateinit var result: ByteArray
        ByteArrayOutputStream().use { byteStream ->
            byteStream.write(iv)            // Put IV to the head

            CipherOutputStream(byteStream, input).use {
                it.write(data)
            }
            result = byteStream.toByteArray()
        }
        return result
    }

    override fun decrypt(data: ByteArray?): ByteArray? {
        if(data == null) {
            return null
        }

        if(data.isEmpty()) {
            return ByteArray(0)
        }

        // Get key
        val secretKey = getKey()

        // Extract initial vector
        val iv = ByteArray(IV_SIZE)
        data.copyInto(iv, 0, 0, IV_SIZE)

        val parameterSpec = GCMParameterSpec(KEY_SIZE, iv)

        val output = Cipher.getInstance(CRYPTO_ALG)
        output.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        val bufferToRead = ByteArray(BLOCK_SIZE)
        lateinit var result: ByteArray

        ByteArrayInputStream(data).use { byteInputStream ->
            byteInputStream.skip(IV_SIZE.toLong())           // Skip IV

            ByteArrayOutputStream().use { byteOutputStream ->
                CipherInputStream(byteInputStream, output).use { decryptedOutputStream ->
                    var bytesRead = decryptedOutputStream.read(bufferToRead)
                    while (bytesRead != -1) {
                        byteOutputStream.write(bufferToRead, 0, bytesRead)
                        bytesRead = decryptedOutputStream.read(bufferToRead)
                    }
                }
                result = byteOutputStream.toByteArray()
            }
        }
        return result
    }

    protected abstract fun getKey(): Key
}