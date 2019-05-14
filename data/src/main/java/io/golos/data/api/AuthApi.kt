package io.golos.data.api

import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.model.UserProfile
import io.golos.cyber4j.services.model.AuthListener

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
interface AuthApi {
    fun setActiveUserCreds(user: CyberName, activeKey: String)

    fun addAuthListener(listener: AuthListener)

    fun getUserAccount(user: CyberName): UserProfile
}
