package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.pin

import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.AuthType
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class PinCodeModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val stringsConverter: StringsConverter,
    @Named(Clarification.AES) private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val fingerprintAuthManager: FingerprintAuthManager
) : PinCodeModel {

    private var primaryCode: String? = null
    private var repeatedCode: String? = null

    override val isFingerprintAuthenticationPossible: Boolean
        get() = fingerprintAuthManager.isAuthenticationPossible

    override fun updatePrimaryCode(code: String?) {
        primaryCode = code
    }

    override fun updateRepeatedCode(code: String?) {
        repeatedCode = code
    }

    override fun isPrimaryCodeCompleted() = primaryCode != null

    override fun isRepeatedCodeCompleted() = repeatedCode != null

    /**
     * @return true if valid
     */
    override fun validate() = primaryCode == repeatedCode

    override suspend fun saveCode(): Boolean =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val codeAsBytes = stringsConverter.toBytes(primaryCode!!)
                val encryptedCode = encryptor.encrypt(codeAsBytes)
                keyValueStorage.savePinCode(encryptedCode!!)

                var newAuthState = keyValueStorage.getAuthState()!!.copy(isPinCodeSettingsPassed = true)

                if(!isFingerprintAuthenticationPossible) {  // Skip fingerprints settings
                    newAuthState = newAuthState.copy(isFingerprintSettingsPassed = true)

                    keyValueStorage.saveAppUnlockWay(AppUnlockWay.PIN_CODE)
                }

                keyValueStorage.saveAuthState(newAuthState)
                true
            } catch (ex: Exception) {
                Timber.e(ex)
                false
            }
        }

    override suspend fun getAuthType(): AuthType =
        withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()!!.type
        }
}