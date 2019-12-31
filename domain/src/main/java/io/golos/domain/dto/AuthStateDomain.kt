package io.golos.domain.dto

import io.golos.domain.Entity

data class AuthStateDomain(
    val userName: String,
    val user: UserIdDomain,
    val isUserLoggedIn: Boolean,
    val isPinCodeSettingsPassed: Boolean,
    val isFingerprintSettingsPassed: Boolean,
    val isKeysExported: Boolean,
    val type: AuthType
) : Entity