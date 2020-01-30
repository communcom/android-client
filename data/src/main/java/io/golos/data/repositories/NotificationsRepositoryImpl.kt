package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.data.mappers.mapToNotificationDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.NotificationsPageDomain
import io.golos.domain.dto.NotificationsStatusDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.NotificationsRepository
import io.golos.utils.toServerFormat
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ApplicationScope
class NotificationsRepositoryImpl @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : RepositoryBase(dispatchersProvider, networkStateChecker), NotificationsRepository {

    private val notificationsCountChannel = ConflatedBroadcastChannel(NotificationsStatusDomain(0, null))

    override suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>{
        val newNotificationsCounter = withContext(dispatchersProvider.ioDispatcher){
            sharedPreferencesStorage.createReadOperationsInstance().readInt(PREF_UNREAD_NOTIFICATIONS_COUNT)
        } ?: 0
        notificationsCountChannel.send(NotificationsStatusDomain(newNotificationsCounter, null))
        return notificationsCountChannel.asFlow()
    }

    override suspend fun markAllNotificationAsViewed(untilDate: Date) {
        apiCall { commun4j.markAllNotificationAsViewed(untilDate.toServerFormat()) }
        updateNewNotificationsCounter(untilDate)
    }

    override suspend fun updateNewNotificationsCounter() {
        updateNewNotificationsCounter(null)
    }

    private suspend fun updateNewNotificationsCounter(lastNotificationMarkDate: Date?){
        val newNotificationsCounter = getNewNotificationsCounter()
        notificationsCountChannel.send(NotificationsStatusDomain(newNotificationsCounter, lastNotificationMarkDate))
    }

    override suspend fun getNewNotificationsCounter(): Int {
        val unseenCount = apiCall { commun4j.getNotificationsStatus() }.unseenCount
        val createWriteOperationsInstance = sharedPreferencesStorage.createWriteOperationsInstance()
        createWriteOperationsInstance.putInt(PREF_UNREAD_NOTIFICATIONS_COUNT, unseenCount)
        createWriteOperationsInstance.commit()
        return unseenCount
    }

    override suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain {
        val notificationsResponse = apiCall{ commun4j.getNotifications(limit, beforeThanDate) }
        val notifications = notificationsResponse.items.mapNotNull { it.mapToNotificationDomain(currentUserRepository.userName) }
        return NotificationsPageDomain(notifications, notificationsResponse.lastNotificationTimestamp)
    }

    private companion object{

        private const val PREF_UNREAD_NOTIFICATIONS_COUNT = "PREF_UNREAD_NOTIFICATIONS_COUNT"
    }
}