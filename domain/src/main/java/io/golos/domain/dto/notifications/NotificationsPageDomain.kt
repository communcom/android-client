package io.golos.domain.dto.notifications

import io.golos.domain.dto.notifications.NotificationDomain

data class NotificationsPageDomain(val notifications: List<NotificationDomain>, val lastNotificationTimeStamp: String?)