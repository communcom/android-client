package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class AuthState(
    val user: CyberUser,
    val isUserLoggedIn: Boolean
) : Entity