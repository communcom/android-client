package io.golos.cyber_android.ui.screens.login.signup.pin

import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import kotlinx.coroutines.withContext

class PinCodeModelImpl(
    private val dispatchersProvider: DispatchersProvider,
    private val stringsConverter: StringsConverter,
    private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val logger: Logger,
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
                }

                keyValueStorage.saveAuthState(newAuthState)
                true
            } catch (ex: Exception) {
                logger(ex)
                false
            }
        }
}