package io.golos.data.api

import io.golos.commun4j.services.model.MobileShowSettings
import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.services.model.UserSettings

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
interface SettingsApi {
    fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk

    fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk

    fun getSettings(deviceId: String): UserSettings
}
