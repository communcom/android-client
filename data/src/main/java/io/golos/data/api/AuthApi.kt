package io.golos.data.api

import io.golos.cyber4j.model.AuthListener
import io.golos.cyber4j.model.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
interface AuthApi {
    fun setActiveUserCreds(user: CyberName, activeKey: String)

    fun addAuthListener(listener: AuthListener)
}
