package io.golos.use_cases.sign_up.transitions

import io.golos.domain.DispatchersProvider
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.FtueBoardStageEntity
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.fingerprint.FingerprintAuthManager
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import kotlinx.coroutines.withContext
import timber.log.Timber

class FromEnteringPinCodeOnPinCodeEntered(
    private val dispatchersProvider: DispatchersProvider,
    parent: SignUpSMCoreTransition,
    private val stringsConverter: StringsConverter,
    private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val fingerprintAuthManager: FingerprintAuthManager,
    private val authUseCase: AuthUseCase
) : SingUpTransitionBase<PinCodeEntered>(parent, SignUpState.ENTERING_PIN) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PinCodeEntered, snapshot: SignUpSnapshotDomain): TransitionResult {
        val pinCode = event.pinCode

        // Store PIN-code
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                val codeAsBytes = stringsConverter.toBytes(pinCode)
                val encryptedCode = encryptor.encrypt(codeAsBytes)
                keyValueStorage.savePinCode(encryptedCode!!)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
            parent.sendCommand(NavigateToSelectMethod())
            return getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
        }

        // Move to the fingerprint step
        if(fingerprintAuthManager.isAuthenticationPossible) {
            parent.sendCommand(NavigateToSelectUnlock())
            return getResult(SignUpState.SELECTING_METHOD_TO_UNLOCK, snapshot.copy(pinCode = pinCode))
        } else {
            // Complete a sign up process

            // Auth
            try {
                parent.sendCommand(ShowLoading())
                authUseCase.auth(snapshot.userName!!, snapshot.password!!)
                parent.sendCommand(HideLoading())
            } catch (ex: Exception) {
                Timber.e(ex)
                parent.sendCommand(HideLoading())
                parent.sendCommand(ShowError(SignUpMessageCode.AUTH_ERROR))
                return getResult()
            }

            // Settings
            try {
                withContext(dispatchersProvider.ioDispatcher) {
                    keyValueStorage.saveAppUnlockWay(AppUnlockWay.PIN_CODE)
                    keyValueStorage.saveFtueBoardStage(FtueBoardStageEntity.NEED_SHOW)
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
                parent.sendCommand(NavigateToSelectMethod())
                return getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
            }

            parent.sendCommand(SingUpCompleted())
            return getResult(SignUpState.FINAL,  clearSnapshot = true)
        }
    }
}