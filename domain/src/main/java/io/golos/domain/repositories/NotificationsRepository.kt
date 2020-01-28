package io.golos.domain.repositories

import io.golos.domain.dto.NotificationsPageDomain
import java.util.*

interface NotificationsRepository {

    suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain

    suspend fun getUnreadNotificationsCount(): Int

    suspend fun markAllNotificationAsViewed(untilDate: Date)
}