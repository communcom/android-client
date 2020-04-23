package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import timber.log.Timber

class FromWaitingForEmailOnEmailEntered(
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository
) : SingUpTransitionBase<EmailEntered>(parent, SignUpState.WAITING_FOR_EMAIL) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: EmailEntered, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            authRepository.firstEmailUserRegistrationStep(event.captcha, event.email)
            parent.sendCommand(HideLoading())

            parent.sendCommand(NavigateToEmailVerification())
            getResult(SignUpState.EMAIL_VERIFICATION, snapshot.copy(email = event.email))
        } catch (ex: Exception) {
            Timber.e(ex)

            parent.sendCommand(HideLoading())

            if(ex is ApiResponseErrorException) {
                when(ex.errorInfo.code) {
                    1101L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.EMAIL_ALREADY_REGISTERED))
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