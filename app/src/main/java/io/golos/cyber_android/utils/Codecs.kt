package io.golos.cyber_android.utils

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.spec.MGF1ParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.security.auth.x500.X500Principal


/**
 * Created by yuri on 10.11.17.
 */


class EnCryptor(private val context: Context) {

    fun encryptText(alias: KeystoreKeyAlias, textToEncrypt: String): ByteArray {
        val cipher = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        } else {
            Cipher.getInstance("RSA/ECB/PKCS1Padding")
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val spec = OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias).public, spec)
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias).public)
        }
        return cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))
    }

    private fun getSecretKey(alias: KeystoreKeyAlias): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", DeCryptor.ANDROID_KEY_STORE)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val spec = KeyGenParameterSpec.Builder(
                alias.name,
                KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setKeyValidityEnd(Date(Long.MAX_VALUE))
                .build()
            generator.initialize(spec)
        } else {
            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias.name)
                .setSubject(X500Principal("CN=Golos cyber, O=Android Cyber"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(Date())
                .setEndDate(Date(Long.MAX_VALUE))
                .build()
            generator.initialize(spec)
        }
        return generator.genKeyPair()
    }
}

class DeCryptor {

    private var keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)

    init {
        keyStore!!.load(null)

    }

    fun decryptData(alias: KeystoreKeyAlias, encryptedData: ByteArray): String {
        try {
            val cipher = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            } else {
                Cipher.getInstance("RSA/ECB/PKCS1Padding")
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val spec = OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT
                )
                cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(alias), spec)
            } else {
                cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(alias))
            }
            return String(cipher.doFinal(encryptedData), Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private fun getPrivateKey(alias: KeystoreKeyAlias): PrivateKey {
        return (keyStore!!.getEntry(alias.name, null) as KeyStore.PrivateKeyEntry).privateKey
    }

    companion object {

        private val TRANSFORMATION = "AES/GCM/NoPadding"
        internal val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}

enum class KeystoreKeyAlias {
    ACTIVE_WIF;

    override fun toString(): String {
        return when (this) {
            ACTIVE_WIF -> "active_key"
        }

    }
}