package io.golos.data.api

import io.golos.cyber4j.model.MobileShowSettings
import io.golos.cyber4j.model.ResultOk
import io.golos.cyber4j.model.UserSettings

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
interface SettingsApi {
    fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk

    fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk

    fun getSettings(deviceId: String): UserSettings
}
