package io.golos.domain

import io.golos.domain.entities.AuthState
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
interface Persister {
    fun saveAuthState(state: AuthState)
    fun getAuthState(): AuthState?

    fun saveActiveKey(activeKey: String)
    fun getActiveKey(): String?

    fun savePushNotifsSettings(forUser: CyberName, settings: PushNotificationsStateModel)
    fun getPushNotifsSettings(forUser: CyberName): PushNotificationsStateModel
}