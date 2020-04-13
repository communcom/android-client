package io.golos.cyber_android.services.firebase.notifications.popup_manager

import io.golos.domain.dto.NotificationDomain

interface FirebaseNotificationPopupManager {
    fun showNotification(notification: NotificationDomain)
}