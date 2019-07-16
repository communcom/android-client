package io.golos.cyber_android.core.key_value_storage

import com.squareup.moshi.Moshi
import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.entities.AuthState
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.sharedmodel.CyberName

/**
 * Helper class for access to App-level private shared preferences
 */
class KeyValueStorageFacadeImpl
constructor(
    private val keyValueStorage: Storage,
    private val moshi: Moshi
) : KeyValueStorageFacade {
    /**
     * Save encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    override fun saveAESCryptoKey(key: ByteArray) =
        keyValueStorage.update {
            it.putBytes("CRYPTO_KEY_AES", key)
        }

    /**
     * Get encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    override fun getAESCryptoKey(): ByteArray? =
        keyValueStorage.read {
            it.readBytes("CRYPTO_KEY_AES")
        }

    override fun saveAuthState(state: AuthState) =
        keyValueStorage.update {
            it.putString("AUTH_STATE", moshi.adapter(AuthState::class.java).toJson(state))
        }

    override fun getAuthState(): AuthState? =
        keyValueStorage.read {
            val authStateString = it.readString("AUTH_STATE") ?: return@read null
            moshi.adapter(AuthState::class.java).fromJson(authStateString)
        }

    override fun saveActiveKey(activeKey: ByteArray) {
        keyValueStorage.update {
            it.putBytes("ACTIVE_KEY", activeKey)
        }
    }

    override fun getActiveKey(): ByteArray? =
        keyValueStorage.read {
            it.readBytes("ACTIVE_KEY")
        }

    override fun savePushNotificationsSettings(forUser: CyberName, settings: PushNotificationsStateModel) =
        keyValueStorage.update {
            it.putString(
                "pushes_of_${forUser.name}",
                moshi.adapter(PushNotificationsStateModel::class.java).toJson(settings)
            )
        }

    override fun getPushNotificationsSettings(forUser: CyberName): PushNotificationsStateModel =
        keyValueStorage.read {
            val authStateString = it.readString("pushes_of_${forUser.name}") ?: return@read PushNotificationsStateModel.DEFAULT

            moshi.adapter(PushNotificationsStateModel::class.java).fromJson(authStateString)
                ?: PushNotificationsStateModel.DEFAULT
        }

    override fun savePinCode(pinCode: ByteArray) =
        keyValueStorage.update {
            it.putBytes("PIN_CODE", pinCode)
        }

    override fun getPinCode(): ByteArray? =
        keyValueStorage.read {
            it.readBytes("PIN_CODE")
        }
}