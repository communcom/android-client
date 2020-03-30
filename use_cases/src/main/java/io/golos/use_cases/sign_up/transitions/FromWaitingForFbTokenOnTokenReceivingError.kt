package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.ShowError
import io.golos.use_cases.sign_up.core.data_structs.SignUpMessageCode
import io.golos.use_cases.sign_up.core.data_structs.TokenReceivingError
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromWaitingForFbTokenOnTokenReceivingError(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<TokenReceivingError>(parent, SignUpState.WAITING_FOR_FB_TOKEN) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: TokenReceivingError, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(ShowError(SignUpMessageCode.TOKEN_RECEIVING_ERROR))
        return getResult(SignUpState.SELECTING_SIGN_UP_METHOD)
    }
}