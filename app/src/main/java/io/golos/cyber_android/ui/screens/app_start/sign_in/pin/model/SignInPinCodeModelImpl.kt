package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model

import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage.PinCodesStorage
import io.golos.domain.fingerprint.FingerprintAuthManager
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.AppUnlockWay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SignInPinCodeModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val stringsConverter: StringsConverter,
    @Named(Clarification.AES) private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val fingerprintAuthManager: FingerprintAuthManager,
    private val pinCodeStorage: PinCodesStorage
) : SignInPinCodeModel {

    override val isFingerprintAuthenticationPossible: Boolean
        get() = fingerprintAuthManager.isAuthenticationPossible

    override fun updatePrimaryCode(code: String?) = pinCodeStorage.updatePrimaryCode(code)

    override fun updateRepeatedCode(code: String?) = pinCodeStorage.updateRepeatedCode(code)

    override fun isPrimaryCodeCompleted() = pinCodeStorage.isPrimaryCodeCompleted()

    override fun isRepeatedCodeCompleted() = pinCodeStorage.isRepeatedCodeCompleted()

    /**
     * @return true if valid
     */
    override fun validate() = pinCodeStorage.validate()

    override suspend fun saveCode(): Boolean =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val codeAsBytes = stringsConverter.toBytes(pinCodeStorage.pinCode!!)
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

    override suspend fun saveKeysExported() {
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val newAuthState = keyValueStorage.getAuthState()!!.copy(isKeysExported = true)
                keyValueStorage.saveAuthState(newAuthState)
            } catch(ex: Exception) {
                Timber.e(ex)
                throw ex
            }
        }
    }
}