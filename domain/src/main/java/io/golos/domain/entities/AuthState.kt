package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class AuthState(
    val user: CyberName,
    val isUserLoggedIn: Boolean,
    val isPinCodeSet: Boolean
) : Entity