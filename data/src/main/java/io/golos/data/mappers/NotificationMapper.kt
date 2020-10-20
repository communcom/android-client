package io.golos.data.mappers

import io.golos.commun4j.services.model.*
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.notifications.*

fun Notification.mapToNotificationDomain(currentUserId: UserIdDomain, currentUserName: String): NotificationDomain {
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
                    communityId = CommunityIdDomain(if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.communityId.value),
                    alias = if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.alias,
                    name = if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.name!!,
                    avatarUrl = if (pointType == "token") null else community!!.avatarUrl,
                    coverUrl = null,
                    subscribersCount = 0,
                    isSubscribed = false,
                    postsCount = 0
                ),
                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }

        is DonationNotification -> {
            DonationNotificationDomain(
                id = id,
                isNew = isNew,
                createTime = timestamp,
                user = from.mapToUserNotificationDomain(),
                amount = amount ?: 0.0,
                pointType = pointType,
                community = CommunityDomain(
                    communityId = CommunityIdDomain(if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.communityId.value),
                    alias = if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.alias,
                    name = if (pointType == "token") GlobalConstants.COMMUN_CODE else community!!.name!!,
                    avatarUrl = if (pointType == "token") null else community!!.avatarUrl,
                    coverUrl = null,
                    subscribersCount = 0,
                    isSubscribed = false,
                    postsCount = 0
                ),
                postId = contentId!!.mapToContentIdDomain(),
                postTextBrief = this.post?.shortText,
                postImageUrl = this.post?.imageUrl,
                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }

        is RewardNotification -> {
            RewardNotificationDomain(
                id = id,
                isNew = isNew,
                createTime = timestamp,
                user = UserNotificationDomain(
                    UserIdDomain(userId.name),
                    null,
                    null
                ),
                amount = amount,

                community = CommunityDomain(
                    communityId = CommunityIdDomain(community!!.communityId.value),
                    alias = community!!.alias,
                    name = community!!.name!!,
                    avatarUrl = community!!.avatarUrl,
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

        is ReferralRegistrationBonusNotification ->
            ReferralRegistrationBonusNotificationDomain(
                id = id,
                isNew = isNew,
                createTime = timestamp,
                user = UserNotificationDomain(
                    id = UserIdDomain(from.userId.name),
                    name = from.username,
                    avatar = from.avatarUrl),
                amount = amount,
                pointType = pointType,
                userId = UserIdDomain(userId.name),
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                referral = UserNotificationDomain(
                    id = UserIdDomain(referral.userId.name),
                    name = referral.username,
                    avatar = referral.avatarUrl)
            )

        is ReferralPurchaseBonusNotification ->
            ReferralPurchaseBonusNotificationDomain(
                id = id,
                isNew = isNew,
                createTime = timestamp,
                user = UserNotificationDomain(
                    id = UserIdDomain(from.userId.name),
                    name = from.username,
                    avatar = from.avatarUrl),
                amount = amount,
                pointType = pointType,
                userId = UserIdDomain(userId.name),
                percent = percent,
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                referral = UserNotificationDomain(
                    id = UserIdDomain(referral.userId.name),
                    name = referral.username,
                    avatar = referral.avatarUrl)
            )

        else -> {
            UnsupportedNotificationDomain(
                id = id,
                isNew = false,
                createTime = timestamp,
                user = UserNotificationDomain(
                    currentUserId,
                    currentUserName,
                    null
                ),
                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }
    }
}