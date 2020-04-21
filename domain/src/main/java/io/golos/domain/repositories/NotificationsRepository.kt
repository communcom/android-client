package io.golos.domain.repositories

import io.golos.domain.dto.notifications.NotificationSettingsDomain
import io.golos.domain.dto.notifications.NotificationsPageDomain
import io.golos.domain.dto.notifications.NotificationsStatusDomain
import kotlinx.coroutines.flow.Flow
import java.util.*

interface NotificationsRepository {
    suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain

    suspend fun updateNewNotificationsCounter()

    suspend fun markAllNotificationAsViewed(untilDate: Date)

    suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>

    suspend fun getNewNotificationsCounter(): Int

    suspend fun subscribeOnNotificationsChanges()

    suspend fun unsubscribeOnNotificationsChanges()

    suspend fun setFcmToken(token: String)

    suspend fun resetFcmToken()

    suspend fun setTimeZoneOffset()

    /**
     * @return full set of settings and their state
     */
    suspend fun getNotificationSettings(): List<NotificationSettingsDomain>

    /**
     * [settings] full set of settings and their state
     */
    suspend fun setNotificationSettings(settings: List<NotificationSettingsDomain>)
}