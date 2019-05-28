package io.golos.domain.entities

import io.golos.cyber4j.model.CyberName
import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class AuthState(
    val user: CyberName,
    val isUserLoggedIn: Boolean
) : Entity