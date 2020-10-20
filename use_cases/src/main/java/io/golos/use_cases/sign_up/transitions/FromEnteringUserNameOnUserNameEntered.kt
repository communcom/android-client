package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import timber.log.Timber

class FromEnteringUserNameOnUserNameEntered(
    parent: SignUpSMCoreTransition,
    private val authRepository: AuthRepository
) : SingUpTransitionBase<UserNameEntered>(parent, SignUpState.ENTERING_USER_NAME) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: UserNameEntered, snapshot: SignUpSnapshotDomain): TransitionResult =
        try {
            parent.sendCommand(ShowLoading())
            val userNameResult = authRepository.setVerifiedUserName(event.userName, snapshot.phoneNumber, snapshot.identity, snapshot.email)
            parent.sendCommand(HideLoading())

            parent.sendCommand(NavigateToGetPassword())

            val newSnapshot = snapshot.copy(userName = event.userName, userId = UserIdDomain(userNameResult.userId.name))
            getResult(SignUpState.ENTERING_PASSWORD, newSnapshot)
        } catch (ex: Exception) {
            parent.sendCommand(HideLoading())
            Timber.e(ex)

            if(ex is ApiResponseErrorException) {
                when(ex.errorInfo.code) {
                    1101L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.ACCOUNT_ALREADY_REGISTERED))
                        parent.sendCommand(NavigateToSelectMethod())
                        getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
                    }
                    1102L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
                        parent.sendCommand(NavigateToSelectMethod())
                        getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
                    }
                    1106L -> {
                        parent.sendCommand(ShowError(SignUpMessageCode.USERNAME_ALREADY_TAKEN))
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