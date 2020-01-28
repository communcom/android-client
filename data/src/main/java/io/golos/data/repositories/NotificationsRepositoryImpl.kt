package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.data.mappers.mapToNotificationDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.NotificationsPageDomain
import io.golos.domain.repositories.NotificationsRepository
import io.golos.utils.toServerFormat
import java.util.*
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker), NotificationsRepository {

    override suspend fun markAllNotificationAsViewed(untilDate: Date) {
        apiCall { commun4j.markAllNotificationAsViewed(untilDate.toServerFormat()) }
    }

    override suspend fun getUnreadNotificationsCount(): Int {
        return apiCall{ commun4j.getNotificationsStatus() }.unseenCount
    }

    override suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain {
        val notificationsResponse = apiCall{ commun4j.getNotifications(limit, beforeThanDate) }
        val notifications = notificationsResponse.items.mapNotNull { it.mapToNotificationDomain() }
        return NotificationsPageDomain(notifications, notificationsResponse.lastNotificationTimestamp)
    }
}