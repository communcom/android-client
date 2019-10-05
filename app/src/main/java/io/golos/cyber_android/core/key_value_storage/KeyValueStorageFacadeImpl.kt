package io.golos.cyber_android.core.key_value_storage

import com.squareup.moshi.Moshi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.entities.AppUnlockWay
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.UserKeyType
import io.golos.domain.requestmodel.PushNotificationsStateModel
import javax.inject.Inject

/**
 * Helper class for access to App-level private shared preferences
 */
class KeyValueStorageFacadeImpl
@Inject
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

    override fun removeAuthState() =
        keyValueStorage.update {
            it.remove("AUTH_STATE")
        }

    override fun saveUserKey(key: ByteArray, keyType: UserKeyType) =
        keyValueStorage.update {
            it.putBytes(getInternalKeyForUserKey(keyType), key)
        }

    override fun getUserKey(keyType: UserKeyType): ByteArray? =
        keyValueStorage.read {
            it.readBytes(getInternalKeyForUserKey(keyType))
        }

    override fun removeUserKey(keyType: UserKeyType) =
        keyValueStorage.update {
            it.remove(getInternalKeyForUserKey(keyType))
        }

    override fun savePushNotificationsSettings(forUser: CyberName, settings: PushNotificationsStateModel) =
        keyValueStorage.update {
            it.putString(
                "PUSHES_OF_${forUser.name}",
                moshi.adapter(PushNotificationsStateModel::class.java).toJson(settings)
            )
        }

    override fun getPushNotificationsSettings(forUser: CyberName): PushNotificationsStateModel =
        keyValueStorage.read {
            val authStateString = it.readString("PUSHES_OF_${forUser.name}") ?: return@read PushNotificationsStateModel.DEFAULT

            moshi.adapter(PushNotificationsStateModel::class.java).fromJson(authStateString)
                ?: PushNotificationsStateModel.DEFAULT
        }

    override fun removePushNotificationsSettings(forUser: CyberName) =
        keyValueStorage.update {
            it.remove("PUSHES_OF_${forUser.name}")
        }

    override fun savePinCode(pinCode: ByteArray) =
        keyValueStorage.update {
            it.putBytes("PIN_CODE", pinCode)
        }

    override fun getPinCode(): ByteArray? =
        keyValueStorage.read {
            it.readBytes("PIN_CODE")
        }

    override fun removePinCode() =
        keyValueStorage.update {
            it.remove("PIN_CODE")
        }

    override fun saveAppUnlockWay(unlockWay: AppUnlockWay) =
        keyValueStorage.update {
            it.putInt("APP_UNLOCK_WAY", unlockWay.value)
        }

    override fun getAppUnlockWay(): AppUnlockWay? =
        keyValueStorage.read {
            it.readInt("APP_UNLOCK_WAY")?.let { value -> AppUnlockWay.createFromValue(value) }
        }

    override fun removeAppUnlockWay() =
        keyValueStorage.update {
            it.remove("APP_UNLOCK_WAY")
        }

    private fun getInternalKeyForUserKey(keyType: UserKeyType) =
        when(keyType) {
            UserKeyType.POSTING -> "USER_KEY_TYPE_POSTING"
            UserKeyType.OWNER -> "USER_KEY_TYPE_OWNER"
            UserKeyType.MEMO -> "USER_KEY_TYPE_MEMO"
            UserKeyType.ACTIVE -> "USER_KEY_TYPE_ACTIVE"
            UserKeyType.MASTER -> "USER_KEY_TYPE_MASTER"
        }
}