package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.dto.sign_up.SignUpType
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.NavigateToPhone
import io.golos.use_cases.sign_up.core.data_structs.PhoneSelected
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromSelectingSignUpMethodOnPhoneSelected(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<PhoneSelected>(parent, SignUpState.SELECTING_SIGN_UP_METHOD) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: PhoneSelected, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(NavigateToPhone())
        return getResult(SignUpState.WAITING_FOR_PHONE, snapshot.copy(type = SignUpType.PHONE))
    }
}