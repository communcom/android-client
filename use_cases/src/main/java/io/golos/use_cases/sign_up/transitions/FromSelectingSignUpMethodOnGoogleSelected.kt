package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.dto.sign_up.SignUpType
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.GoogleSelected
import io.golos.use_cases.sign_up.core.data_structs.StartGoogleSignIn
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromSelectingSignUpMethodOnGoogleSelected(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<GoogleSelected>(parent, SignUpState.SELECTING_SIGN_UP_METHOD) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: GoogleSelected, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(StartGoogleSignIn())
        return getResult(SignUpState.WAITING_FOR_GOOGLE_TOKEN, snapshot.copy(type = SignUpType.GOOGLE))
    }
}