package io.golos.cyber_android.core.encryption.rsa

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import io.golos.domain.Encryptor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

/**
 * Encryption/Decryption via RSA
 */
class EncryptorRSA
constructor(
    private val appContext: Context
) : Encryptor {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "golos_encryption_key_rsa"
        private const val CRYPTO_ALG = "RSA/ECB/PKCS1Padding"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)

    init {
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createInternalKey()
        }
    }

    override fun encrypt(data: ByteArray?): ByteArray? {
        if(data == null) {
            return null
        }

        if(data.isEmpty()) {
            return ByteArray(0)
        }

        val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val publicKey = privateKeyEntry.certificate.publicKey

        val input = Cipher.getInstance(CRYPTO_ALG)
        input.init(Cipher.ENCRYPT_MODE, publicKey)

        lateinit var result: ByteArray
        ByteArrayOutputStream().use { byteStream ->
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

        val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey

        val output = Cipher.getInstance(CRYPTO_ALG)
        output.init(Cipher.DECRYPT_MODE, privateKey)

        val values = mutableListOf<Byte>()
        ByteArrayInputStream(data).use { byteStream ->
            CipherInputStream(byteStream, output).use {
                while (true) {
                    val nextByte = it.read()
                    if(nextByte==-1)
                        break
                    values.add(nextByte.toByte())
                }
            }
        }
        return values.toByteArray()
    }

    @Throws(InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    private fun createInternalKey() {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)

        val generator = KeyPairGenerator.getInstance("RSA",
            KEYSTORE_PROVIDER
        )

        val spec = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec
                .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setCertificateSubject(X500Principal("CN=Sample Name, O=Android Authority"))
                .setCertificateSerialNumber(BigInteger.ONE)
                .setCertificateNotBefore(start.time)
                .setCertificateNotAfter(end.time)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
        } else {
            @Suppress("DEPRECATION")
            KeyPairGeneratorSpec.Builder(appContext)
                .setAlias(KEY_ALIAS)
                .setSubject(X500Principal("CN=Sample Name, O=Android Authority"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
        }

        generator.initialize(spec)

        generator.generateKeyPair()
    }
}