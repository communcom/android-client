package io.golos.domain.dto

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class AuthState(
    val userName: String,
    val user: UserIdDomain,
    val isUserLoggedIn: Boolean,
    val isPinCodeSettingsPassed: Boolean,
    val isFingerprintSettingsPassed: Boolean,
    val isKeysExported: Boolean,
    val type: AuthType
) : Entity