package io.golos.data.mappers

import io.golos.commun4j.services.model.*
import io.golos.domain.dto.*

fun Notification.mapToNotificationDomain(): NotificationDomain? {
    return when (this) {
        is UpvoteNotification -> {
            val authorNotification = voter!!
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            val post = post?.mapToPostNotificationDomain()
            UpVoteNotificationDomain(id, isNew, timestamp, userNotificationDomain, post)
        }
        is MentionNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            val post = post?.mapToPostNotificationDomain()
            MentionNotificationDomain(id, isNew, timestamp, userNotificationDomain, post)
        }
        is ReplyNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            val post = post?.mapToPostNotificationDomain()
            ReplyNotificationDomain(id, isNew, timestamp, userNotificationDomain, post)
        }
        is SubscribeNotification -> {
            val authorNotification = user
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            SubscribeNotificationDomain(id, isNew, timestamp, userNotificationDomain)
        }

        else -> null
    }
}