package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.repositories.NotificationsRepository
import java.util.*
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker), NotificationsRepository {

    override suspend fun getNotifications(pageKey: String?, limit: Int): List<BaseNotificationDomain> {
        val notifications = mutableListOf<BaseNotificationDomain>()
        val calendar = Calendar.getInstance()
        notifications.add(SubscribeNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.SUBSCRIBE,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        notifications.add(ReplyNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.REPLY,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        notifications.add(MentionNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.MENTION,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        notifications.add(UpVoteNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.UP_VOTE,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        notifications.add(SubscribeNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.SUBSCRIBE,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        notifications.add(SubscribeNotificationDomain(UUID.randomUUID().toString(),
            NotificationTypeDomain.SUBSCRIBE,
            true,
            calendar.time,
            calendar.time.toString(),
            UserNotificationDomain(UserIdDomain("cmn1kveyyequ"), "inigomontoya", "https://img.commun.com/images/3YFJrdAL9CvaXhAao8v93tsLdsfo.jpg")))

        return notifications
    }
}