package io.golos.data.persistence.key_value_storage

import com.squareup.moshi.Moshi
import io.golos.domain.dto.CommunityEntity
import io.golos.domain.dto.CommunitySubscriptionsEntity
import io.golos.domain.dto.FtueBoardStageEntity
import io.golos.data.persistence.key_value_storage.storages.Storage
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.UserKeyType
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

    override fun saveAuthState(state: AuthStateDomain) =
        keyValueStorage.update {
            it.putString("AUTH_STATE", moshi.adapter(AuthStateDomain::class.java).toJson(state))
        }

    override fun getAuthState(): AuthStateDomain? =
        keyValueStorage.read {
            val authStateString = it.readString("AUTH_STATE") ?: return@read null
            moshi.adapter(AuthStateDomain::class.java).fromJson(authStateString)
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

    override fun saveFtueBoardStage(stage: FtueBoardStageEntity) =
        keyValueStorage.update {
            it.putString("KEY_FTUE_BOARD_STAGE", stage.name)
        }

    override fun getFtueBoardStage(): FtueBoardStageEntity =
        keyValueStorage.read {
            FtueBoardStageEntity.valueOf(it.readString("KEY_FTUE_BOARD_STAGE") ?: FtueBoardStageEntity.IDLE.name)
        }

    override fun saveFtueCommunitySubscriptions(communitySubscriptions: List<CommunityEntity>) {
        keyValueStorage.update {
            val communitySubscriptionsEntity =
                CommunitySubscriptionsEntity(communitySubscriptions)
            val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
            val json = adapter.toJson(communitySubscriptionsEntity)
            it.putString("KEY_FTUE_COMMUNITY_SUBSCRIPTIONS", json)
        }
    }

    override fun getFtueCommunitySubscriptions(): List<CommunityEntity> =
        keyValueStorage.read { storage ->
            val communitySubscriptionsJson = storage.readString("KEY_FTUE_COMMUNITY_SUBSCRIPTIONS")
            val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
            communitySubscriptionsJson?.let { adapter.fromJson(it)?.communities } ?: listOf()
        }

    override fun removeFtueState() =
        keyValueStorage.update {
            it.remove("KEY_FTUE_BOARD_STAGE")
            it.remove("KEY_FTUE_COMMUNITY_SUBSCRIPTIONS")
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