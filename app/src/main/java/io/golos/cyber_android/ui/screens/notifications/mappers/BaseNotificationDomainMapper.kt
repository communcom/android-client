package io.golos.cyber_android.ui.screens.notifications.mappers

import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.domain.dto.notifications.*
import io.golos.utils.id.IdUtil

fun NotificationDomain.mapToVersionedListItem(): BaseNotificationItem {
    return when(this){
        is TransferNotificationDomain -> {
            TransferNotificationItem(
                version = 0,
                id = IdUtil.generateLongId(),
                isFirstItem = false,
                isLastItem = false,

                notificationId = id,
                createTime = createTime,
                isNew = isNew,

                userId = user.id.userId,
                userName = user.name,
                userAvatar = user.avatar,

                receiver = receiver,
                amount = amount,
                pointType = pointType,

                communityId = community.communityId,
                communityAlias = community.alias!!,
                communityName = community.name,
                communityAvatarUrl = community.avatarUrl,

                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }

        is RewardNotificationDomain -> {
            RewardNotificationItem(
                version = 0,
                id = IdUtil.generateLongId(),
                isFirstItem = false,
                isLastItem = false,

                notificationId = id,
                createTime = createTime,
                isNew = isNew,

                userId = user.id.userId,
                userName = user.name,
                userAvatar = user.avatar,

                amount = amount,

                communityId = community.communityId,
                communityAlias = community.alias!!,
                communityName = community.name,
                communityAvatarUrl = community.avatarUrl,

                currentUserId = currentUserId,
                currentUserName = currentUserName
            )
        }

        is MentionNotificationDomain -> {
            MentionNotificationItem(
                0,
                IdUtil.generateLongId(),
                false,
                false,
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment,
                post,
                currentUserId,
                currentUserName
            )
        }
        is SubscribeNotificationDomain -> SubscribeNotificationItem(
            0,
            IdUtil.generateLongId(),
            false,
            false,
            id,
            createTime,
            isNew,
            user.id.userId,
            user.name,
            user.avatar)

        is UpVoteNotificationDomain -> {
            UpVoteNotificationItem(
                0,
                IdUtil.generateLongId(),
                false,
                false,
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment,
                post,
                currentUserId,
                currentUserName)
        }
        is ReplyNotificationDomain -> {
            ReplyNotificationItem(
                0,
                IdUtil.generateLongId(),
                false,
                false,
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment,
                currentUserId,
                currentUserName)
        }
        is UnsupportedNotificationDomain -> {
            UnsupportedNotificationItem(
                version = 0,
                id = IdUtil.generateLongId(),
                isFirstItem = false,
                isLastItem = false,

                notificationId = id,
                createTime = createTime,
                isNew = isNew,
                userId = currentUserId.userId,
                userName = currentUserName,
                userAvatar = null
            )
        }
        is ReferralPurchaseBonusNotificationDomain ->
            ReferralPurchaseBonusNotificationItem(
                version = 0,
                id = IdUtil.generateLongId(),
                isFirstItem = false,
                isLastItem = false,

                notificationId = id,
                createTime = createTime,
                isNew = isNew,

                userId = user.id.userId,
                userName = user.name,
                userAvatar = user.avatar,

                amount = amount,
                pointType = pointType,
                secondUserId = userId,
                percent = percent,

                currentUserId = currentUserId,
                currentUserName = currentUserName,

                referral = referral
            )
        is ReferralRegistrationBonusNotificationDomain ->
            ReferralRegistrationBonusNotificationItem(
                version = 0,
                id = IdUtil.generateLongId(),
                isFirstItem = false,
                isLastItem = false,

                notificationId = id,
                createTime = createTime,
                isNew = isNew,

                userId = user.id.userId,
                userName = user.name,
                userAvatar = user.avatar,

                amount = amount,
                pointType = pointType,
                secondUserId = userId,

                currentUserId = currentUserId,
                currentUserName = currentUserName,

                referral = referral
            )
    }
}
