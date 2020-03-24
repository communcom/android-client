package io.golos.data.mappers

import io.golos.commun4j.services.model.*
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.*

fun Notification.mapToNotificationDomain(currentUserId: UserIdDomain, currentUserName: String): NotificationDomain? {
    return when (this) {
        is TransferNotification -> {
            TransferNotificationDomain(
                id = id,
                isNew = isNew,
                createTime = timestamp,
                user = from!!.mapToUserNotificationDomain(),
                receiver = UserIdDomain(userId.name),
                amount = amount ?: 0.0,
                pointType = pointType,
                community = CommunityDomain(
                    communityId = CommunityIdDomain(if(pointType == "token") GlobalConstants.COMMUN_CODE else "POLITIC"),
                    alias = if(pointType == "token") GlobalConstants.COMMUN_CODE else "politics",
                    name = if(pointType == "token") GlobalConstants.COMMUN_CODE else "Politics",
                    avatarUrl = if(pointType == "token") null else "https://img.commun.com/images/4KKop1gfty5Uov6qnVESc82s2UHr.jpg",
                    coverUrl = null,
                    subscribersCount = 0,
                    isSubscribed = false,
                    postsCount = 0
                ),
                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }

        is UpvoteNotification -> {
            val authorNotification = voter!!
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            UpVoteNotificationDomain(
                id,
                isNew,
                timestamp,
                userNotificationDomain,
                comment?.mapToNotificationCommentDomain(),
                post?.mapToNotificationPostDomain(),
                UserIdDomain(userId.name),
                currentUserName
            )

        }
        is MentionNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            MentionNotificationDomain(
                id,
                isNew,
                timestamp,
                userNotificationDomain,
                comment?.mapToNotificationCommentDomain(),
                post?.mapToNotificationPostDomain(),
                UserIdDomain(userId.name),
                currentUserName
            )
        }
        is ReplyNotification -> {
            val authorNotification = author
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            ReplyNotificationDomain(
                id,
                isNew,
                timestamp,
                userNotificationDomain,
                comment!!.mapToNotificationCommentDomain(),
                UserIdDomain(userId.name),
                currentUserName
            )
        }
        is SubscribeNotification -> {
            val authorNotification = user
            val userNotificationDomain = UserNotificationDomain(
                UserIdDomain(authorNotification.userId.name),
                authorNotification.username,
                authorNotification.avatarUrl
            )
            SubscribeNotificationDomain(
                id,
                isNew,
                timestamp,
                userNotificationDomain,
                UserIdDomain(userId.name),
                currentUserName
            )
        }

        else -> null
    }
}