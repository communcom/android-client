package io.golos.data.api.settings

import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.MobileShowSettings
import io.golos.commun4j.services.model.ResultOk
import io.golos.commun4j.services.model.UserSettings
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import javax.inject.Inject

class SettingsApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), SettingsApi {

    override fun setBasicSettings(deviceId: String, basic: Map<String, String>): ResultOk =
        commun4j.setUserSettings(deviceId, basic, null, null).getOrThrow()

    override fun setNotificationSettings(deviceId: String, notifSettings: MobileShowSettings): ResultOk =
        commun4j.setUserSettings(deviceId, null, null, notifSettings).getOrThrow()

    override fun getSettings(deviceId: String): UserSettings = commun4j.getUserSettings(deviceId).getOrThrow()
}