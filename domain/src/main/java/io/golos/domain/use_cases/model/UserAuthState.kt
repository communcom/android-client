package io.golos.domain.use_cases.model

import io.golos.commun4j.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-29.
 */
data class UserAuthState(val isUserLoggedIn: Boolean, val userName: CyberName)