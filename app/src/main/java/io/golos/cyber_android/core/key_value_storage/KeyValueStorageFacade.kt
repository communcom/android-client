package io.golos.cyber_android.core.key_value_storage

import io.golos.domain.entities.AuthState
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.sharedmodel.CyberName

interface KeyValueStorageFacade {
    /**
     * Save encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    fun saveAESCryptoKey(key: ByteArray)

    /**
     * Get encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    fun getAESCryptoKey(): ByteArray?

    fun saveAuthState(state: AuthState)

    fun getAuthState(): AuthState?

    fun saveActiveKey(activeKey: ByteArray)

    fun getActiveKey(): ByteArray?

    fun savePushNotificationsSettings(forUser: CyberName, settings: PushNotificationsStateModel)

    fun getPushNotificationsSettings(forUser: CyberName): PushNotificationsStateModel

    fun savePinCode(pinCode: ByteArray)

    fun getPinCode(): ByteArray?
}