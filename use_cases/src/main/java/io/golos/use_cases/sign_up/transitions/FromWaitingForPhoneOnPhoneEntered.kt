package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import timber.log.Timber

class FromWaitingForPhoneOnPhoneEntered(
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository
) : SingUpTransitionBase<PhoneEntered>(parent, SignUpState.WAITING_FOR_PHONE) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PhoneEntered, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            authRepository.firstUserRegistrationStep(event.captcha, event.phone)
            parent.sendCommand(HideLoading())

            parent.sendCommand(NavigateToPhoneVerification())
            getResult(SignUpState.PHONE_VERIFICATION, snapshot.copy(phoneNumber = event.phone))
        } catch (ex: Exception) {
            Timber.e(ex)

            parent.sendCommand(HideLoading())

            if(ex is ApiResponseErrorException) {
                when(ex.errorInfo.code) {
                    1101L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.PHONE_ALREADY_REGISTERED))
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