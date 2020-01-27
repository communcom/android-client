package io.golos.domain.repositories

import io.golos.domain.dto.BaseNotificationDomain

interface NotificationsRepository {

    suspend fun getNotifications(limit: Int): List<BaseNotificationDomain>
}