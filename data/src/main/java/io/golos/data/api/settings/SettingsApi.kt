package io.golos.data.api.settings

import io.golos.commun4j.services.model.MobileShowSettings
import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.services.model.UserSettings

interface SettingsApi {
    fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk

    fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk

    fun getSettings(deviceId: String): UserSettings
}
