package io.golos.cyber_android.core.key_value_storage

import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.cyber_android.core.strings_converter.StringsConverter

/**
 * Helper class for access to App-level private shared preferences
 */
class KeyValueStorageFacadeImpl
constructor(
    private val keyValueStorage: Storage,
    private val stringsConverter: StringsConverter
) : KeyValueStorageFacade {

    private object Keys {
        /**
         * AES-encryption key (for API < 23)
         */
        const val CRYPTO_KEY_AES = "CRYPTO_KEY_AES"
    }

    override fun setAESCryptoKey(key: ByteArray) =
        keyValueStorage.update {
            it.putString(Keys.CRYPTO_KEY_AES, stringsConverter.toBase64(key))
        }

    override fun getAESCryptoKey(): ByteArray? =
        keyValueStorage.read {
            it.readString(Keys.CRYPTO_KEY_AES)?.let { keyAsString -> stringsConverter.fromBase64(keyAsString) }
        }
}