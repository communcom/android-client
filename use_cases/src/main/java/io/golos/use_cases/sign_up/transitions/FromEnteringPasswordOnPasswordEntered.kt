package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.NavigateToConfirmPassword
import io.golos.use_cases.sign_up.core.data_structs.PasswordEntered
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromEnteringPasswordOnPasswordEntered(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<PasswordEntered>(parent, SignUpState.ENTERING_PASSWORD) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PasswordEntered, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(NavigateToConfirmPassword())
        return getResult(SignUpState.PASSWORD_CONFIRMATION, snapshot.copy(password = event.password))
    }
}