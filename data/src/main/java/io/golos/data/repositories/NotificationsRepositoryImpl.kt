package io.golos.data.repositories

import com.squareup.moshi.Moshi
import io.golos.commun4j.Commun4j
import io.golos.commun4j.services.model.UnsupportedNotification
import io.golos.data.ServerMessageReceiver
import io.golos.data.api.ApiMethods
import io.golos.data.dto.NotificationsStatusEntity
import io.golos.data.mappers.mapToNotificationDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.NotificationDomain
import io.golos.domain.dto.NotificationsPageDomain
import io.golos.domain.dto.NotificationsStatusDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.NotificationsRepository
import io.golos.utils.format.DatesServerFormatter
import io.golos.utils.helpers.add
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@ApplicationScope
class NotificationsRepositoryImpl @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage,
    private val serverMessageReceiver: ServerMessageReceiver,
    private val moshi: Moshi
) : RepositoryCoroutineSupport(
    dispatchersProvider,
    networkStateChecker
) , NotificationsRepository {

    private companion object{
        private const val PREF_UNREAD_NOTIFICATIONS_COUNT = "PREF_UNREAD_NOTIFICATIONS_COUNT"
    }

    private val notificationsCountChannel = ConflatedBroadcastChannel(NotificationsStatusDomain(0, null))

    init {
        launch {
            serverMessageReceiver
                .messagesFlow()
                .collect {
                    if(it.method == ApiMethods.notificationsStatusUpdated){
                        val notificationsStatusEntity = moshi.adapter(NotificationsStatusEntity::class.java).fromJsonValue(it.data)
                        notificationsStatusEntity?.let {
                            val newNotificationsCounter = notificationsStatusEntity.unseenCount.toInt()
                            launch {
                                notificationsCountChannel.send(NotificationsStatusDomain(newNotificationsCounter, null))
                                saveNewNotificationCounter(newNotificationsCounter)
                            }
                        }
                    }
                }
        }
    }

    override suspend fun subscribeOnNotificationsChanges() {
        apiCall {
            commun4j.subscribeOnNotifications()
        }
    }

    override suspend fun unsubscribeOnNotificationsChanges() {
        apiCall {
            commun4j.unSubscribeFromNotifications()
        }
    }

    override suspend fun getNewNotificationsCounterFlow(): Flow<NotificationsStatusDomain>{
        val newNotificationsCounter = withContext(dispatchersProvider.ioDispatcher){
            sharedPreferencesStorage.createReadOperationsInstance().readInt(PREF_UNREAD_NOTIFICATIONS_COUNT)
        } ?: 0
        notificationsCountChannel.send(NotificationsStatusDomain(newNotificationsCounter, null))
        return notificationsCountChannel.asFlow()
    }

    override suspend fun markAllNotificationAsViewed(untilDate: Date) {
        apiCall { commun4j.markAllNotificationAsViewed(DatesServerFormatter.formatToServer(untilDate)) }
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
        saveNewNotificationCounter(unseenCount)
        return unseenCount
    }

    override suspend fun getNotifications(beforeThanDate: String?, limit: Int): NotificationsPageDomain {
        val notificationsResponse = apiCall{ commun4j.getNotificationsSkipUnrecognized(limit, beforeThanDate) }

        return if(notificationsResponse.items.isNotEmpty()) {
            val lastTimestamp = notificationsResponse.items.last().timestamp.let {
                DatesServerFormatter.formatToServer(it.add(-1000))
            }

            val notifications = notificationsResponse.items.map {
                it.mapToNotificationDomain(
                    currentUserRepository.userId,
                    currentUserRepository.userName
                )
            }

            NotificationsPageDomain(notifications, lastTimestamp)
        } else {
            NotificationsPageDomain(listOf(), null)
        }
    }

    override suspend fun setFcmToken(token: String) {
        Timber.tag("FCM_MESSAGES").d("Token: setFcmToken(${token})")
        apiCall { commun4j.setFcmToken(token) }
    }

    override suspend fun resetFcmToken() {
        apiCall { commun4j.resetFcmToken() }
    }

    override suspend fun setTimeZoneOffset() {
        val offset = -Calendar.getInstance().timeZone.rawOffset / (1000 * 60)
        Timber.tag("FCM_MESSAGES").d("Token: setTimeZoneOffset(${offset})")
        apiCall { commun4j.setInfo(offset) }
    }

    private fun saveNewNotificationCounter(counter: Int){
        val createWriteOperationsInstance = sharedPreferencesStorage.createWriteOperationsInstance()
        createWriteOperationsInstance.putInt(PREF_UNREAD_NOTIFICATIONS_COUNT, counter)
        createWriteOperationsInstance.commit()
    }
}