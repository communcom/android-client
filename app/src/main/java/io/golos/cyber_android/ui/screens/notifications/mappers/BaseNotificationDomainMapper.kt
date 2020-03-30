package io.golos.cyber_android.ui.screens.notifications.mappers

import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.*
import io.golos.utils.id.IdUtil

fun NotificationDomain.mapToVersionedListItem(): VersionedListItem {
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
            NotificationMentionItem(
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
        is SubscribeNotificationDomain -> NotificationSubscribeItem(
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
            NotificationUpVoteItem(
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
            NotificationReplyItem(
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
    }
}
