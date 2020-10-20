package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import timber.log.Timber

class FromPhoneVerificationOnCodeEntered(
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository
) : SingUpTransitionBase<PhoneVerificationCodeEntered>(parent, SignUpState.PHONE_VERIFICATION) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PhoneVerificationCodeEntered, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            authRepository.verifyPhoneForUserRegistration(snapshot.phoneNumber!!, event.verificationCode)
            parent.sendCommand(HideLoading())

            parent.sendCommand(NavigateToUserName())
            getResult(SignUpState.ENTERING_USER_NAME)
        } catch (ex: Exception) {
            Timber.e(ex)

            parent.sendCommand(HideLoading())

            if(ex is ApiResponseErrorException) {
                when(ex.errorInfo.code) {
                    1104L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.INVALID_PHONE_VERIFICATION_CODE))
                        getResult()
                    }
                    else -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
                        parent.sendCommand(NavigateToSelectMethod())
                        getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
                    }
                }
            } else {
                parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
                getResult()
            }
        }
}