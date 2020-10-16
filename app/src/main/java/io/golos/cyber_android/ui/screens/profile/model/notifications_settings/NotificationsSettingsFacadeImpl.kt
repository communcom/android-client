package io.golos.cyber_android.ui.screens.profile.model.notifications_settings

import io.golos.domain.dto.notifications.NotificationSettingsDomain
import io.golos.domain.repositories.NotificationsRepository
import javax.inject.Inject

class NotificationsSettingsFacadeImpl
@Inject
constructor(
    private val notificationsRepository: NotificationsRepository
) : NotificationsSettingsFacade {

    private var oldSettings = listOf<NotificationSettingsDomain>()

    override suspend fun getSettings(): List<NotificationSettingsDomain> {
        val settings = notificationsRepository.getNotificationSettings()
        oldSettings = settings
        return settings
    }

    override suspend fun updateSettings(newSettings: List<NotificationSettingsDomain>) {
        if(hasChanges(oldSettings, newSettings)) {
            notificationsRepository.setNotificationSettings(newSettings)
        }
    }

    private fun hasChanges(oldSettings: List<NotificationSettingsDomain>, newSettings: List<NotificationSettingsDomain>): Boolean {
        oldSettings.forEach { old ->
            if(!newSettings.any { it == old }) {
                return true
            }
        }

        return false
    }
}