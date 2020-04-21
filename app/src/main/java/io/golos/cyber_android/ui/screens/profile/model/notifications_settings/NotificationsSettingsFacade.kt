package io.golos.cyber_android.ui.screens.profile.model.notifications_settings

import io.golos.domain.dto.notifications.NotificationSettingsDomain

interface NotificationsSettingsFacade {
    suspend fun getSettings(): List<NotificationSettingsDomain>
    suspend fun updateSettings(newSettings: List<NotificationSettingsDomain>)
}