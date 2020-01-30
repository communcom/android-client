package io.golos.domain.repositories

import io.golos.domain.dto.NotificationsPageDomain
import io.golos.domain.dto.NotificationsStatusDomain
import kotlinx.coroutines.flow.Flow
import java.util.*

interface NotificationsRepository {

    suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain

    suspend fun updateNewNotificationsCounter()

    suspend fun markAllNotificationAsViewed(untilDate: Date)

    suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>

    suspend fun getNewNotificationsCounter(): Int
}