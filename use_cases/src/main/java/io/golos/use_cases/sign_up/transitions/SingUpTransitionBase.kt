package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.dto.TransitionResult
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.SingUpEvent

abstract class SingUpTransitionBase<TE: SingUpEvent>(
    protected val parent: SignUpSMCoreTransition,
    private val currentState: SignUpState
): SingUpTransition<TE> {

    protected fun getResult(
        state: SignUpState = currentState,
        snapshot: SignUpSnapshotDomain? = null,
        clearSnapshot: Boolean = false) = TransitionResult(state, snapshot, clearSnapshot)
}