package io.golos.domain.repositories

import io.golos.domain.dto.NotificationsPageDomain

interface NotificationsRepository {

    suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain
}