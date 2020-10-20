package io.golos.cyber_android.ui.screens.profile.model.notifications_settings

import io.golos.domain.dto.notifications.NotificationSettingsDomain
import javax.inject.Inject

class NotificationsSettingsFacadeExternalUserImpl
@Inject
constructor() : NotificationsSettingsFacade {
    override suspend fun getSettings(): List<NotificationSettingsDomain> {
        throw UnsupportedOperationException("This operation is not supported")
    }

    override suspend fun updateSettings(newSettings: List<NotificationSettingsDomain>) {
        throw UnsupportedOperationException("This operation is not supported")
    }
}