package io.golos.use_cases.sign_up.dto

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState

data class TransitionResult(
    val newState: SignUpState,

    /**
     * null if a snapshot has not been updated
     */
    val snapshot: SignUpSnapshotDomain?,

    /**
     * The snapshot need to be deleted
     */
    val clearSnapshot: Boolean
)