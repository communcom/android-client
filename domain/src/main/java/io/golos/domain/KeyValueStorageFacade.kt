package io.golos.domain

import io.golos.domain.dto.*

interface KeyValueStorageFacade {
    /**
     * Save encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    fun saveAESCryptoKey(key: ByteArray)

    /**
     * Get encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    fun getAESCryptoKey(): ByteArray?

    fun saveAuthState(state: AuthStateDomain)
    fun getAuthState(): AuthStateDomain?
    fun removeAuthState()

    fun isAuthStateSavedFromNativeApp(): Boolean

    fun saveUserKey(key: ByteArray, keyType: UserKeyType)
    fun getUserKey(keyType: UserKeyType): ByteArray?
    fun removeUserKey(keyType: UserKeyType)

    fun savePinCode(pinCode: ByteArray)
    fun getPinCode(): ByteArray?
    fun removePinCode()

    fun saveAppUnlockWay(unlockWay: AppUnlockWay)
    fun getAppUnlockWay(): AppUnlockWay?
    fun removeAppUnlockWay()

    fun saveFtueBoardStage(stage: FtueBoardStageEntity)
    fun getFtueBoardStage(): FtueBoardStageEntity

    fun saveFtueCommunitySubscriptions(communitySubscriptions: List<CommunityEntity>)
    fun getFtueCommunitySubscriptions(): List<CommunityEntity>

    fun removeFtueState()
}