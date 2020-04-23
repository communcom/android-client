package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import timber.log.Timber

class FromEmailVerificationOnResendCode(
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository
) : SingUpTransitionBase<EmailVerificationCodeResend>(parent, SignUpState.EMAIL_VERIFICATION) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: EmailVerificationCodeResend, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            authRepository.resendEmail(snapshot.email!!)
            parent.sendCommand(HideLoading())

            parent.sendCommand(ShowMessage(SignUpMessageCode.EMAIL_VERIFICATION_CODE_RESENT))
            parent.sendCommand(EmailVerificationCodeResendCompleted())
            getResult()
        } catch (ex: Exception) {
            Timber.e(ex)

            parent.sendCommand(HideLoading())

            if(ex is ApiResponseErrorException) {
                when(ex.errorInfo.code) {
                    1101L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.ACCOUNT_ALREADY_REGISTERED))
                        parent.sendCommand(NavigateToSelectMethod())
                        getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
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