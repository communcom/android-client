package io.golos.domain.dto.sign_up

import io.golos.domain.dto.UserIdDomain

/**
 * Current state of SignUp SM
 */
data class SignUpSnapshotDomain(
    val state: SignUpState,
    val type: SignUpType?,

    /**
     * An identity in a social network
     */
    val identity: String?,

    val phoneNumber: String?,
    val email: String?,

    val userName: String?,
    val userId: UserIdDomain?,

    val password: String?,

    val pinCode: String?
)