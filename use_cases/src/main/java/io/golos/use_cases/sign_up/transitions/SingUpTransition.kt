package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.use_cases.sign_up.core.data_structs.SingUpEvent
import io.golos.use_cases.sign_up.dto.TransitionResult

interface SingUpTransition<TE: SingUpEvent> {
    /**
     * Process event and return new state of the SM
     */
    suspend fun process(event: TE, snapshot: SignUpSnapshotDomain): TransitionResult
}