package io.golos.domain.interactors.model

import io.golos.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
data class UserAuthState(val isUserLoggedIn: Boolean, val userName: CyberName)