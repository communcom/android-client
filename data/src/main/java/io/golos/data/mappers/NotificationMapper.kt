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
            UpVoteNotificationDomain(id, isNew, timestamp, userNotificationDomain, comment!!.mapToNotificationCommentDomain())
        }
        is MentionNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            MentionNotificationDomain(id, isNew, timestamp, userNotificationDomain, comment!!.mapToNotificationCommentDomain())
        }
        is ReplyNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            ReplyNotificationDomain(id, isNew, timestamp, userNotificationDomain, comment!!.mapToNotificationCommentDomain())
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