package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.dto.sign_up.SignUpType
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.EmailSelected
import io.golos.use_cases.sign_up.core.data_structs.NavigateToEmail
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromSelectingSignUpMethodOnEmailSelected(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<EmailSelected>(parent, SignUpState.SELECTING_SIGN_UP_METHOD) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: EmailSelected, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(NavigateToEmail())
        return getResult(SignUpState.WAITING_FOR_EMAIL, snapshot.copy(type = SignUpType.EMAIL))
    }
}