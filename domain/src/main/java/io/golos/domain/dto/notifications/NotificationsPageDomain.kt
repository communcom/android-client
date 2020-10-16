package io.golos.domain.dto.notifications

data class NotificationsPageDomain(val notifications: List<NotificationDomain>, val lastNotificationTimeStamp: String?)