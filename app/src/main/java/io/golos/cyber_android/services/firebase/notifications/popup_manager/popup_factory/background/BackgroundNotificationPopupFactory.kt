package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.background

import io.golos.domain.dto.NotificationDomain
import timber.log.Timber

class BackgroundNotificationPopupFactory {
    fun showNotification(notification: NotificationDomain) {
        Timber.tag("FCM_MESSAGES").d("Show background popup")
    }
}