package io.golos.cyber_android.ui.screens.notifications.mappers

import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationMentionItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationReplyItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationUpVoteItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.*
import io.golos.domain.utils.IdUtil

fun NotificationDomain.mapToVersionedListItem(): VersionedListItem {
    return when(this){
        is MentionNotificationDomain -> {
            val comment = comment
            val parents = comment.parents
            NotificationMentionItem(0,
                IdUtil.generateLongId(),
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment.shortText,
                comment.imageUrl,
                commentContentId = comment.contentId.mapToContentId(),
                parentCommentContentId = parents.comment?.mapToContentId(),
                parentPostContentId = parents.post.mapToContentId()
            )
        }
        is SubscribeNotificationDomain -> NotificationSubscribeItem(0, IdUtil.generateLongId(), id, createTime, isNew, user.id.userId, user.name, user.avatar)
        is UpVoteNotificationDomain -> {
            val comment = comment
            val parents = comment.parents
            NotificationUpVoteItem(0,
                IdUtil.generateLongId(),
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment.shortText,
                comment.imageUrl,
                commentContentId = comment.contentId.mapToContentId(),
                parentCommentContentId = parents.comment?.mapToContentId(),
                parentPostContentId = parents.post.mapToContentId())
        }
        is ReplyNotificationDomain -> {
            val comment = comment
            val parents = comment.parents
            NotificationReplyItem(0,
                IdUtil.generateLongId(),
                id,
                createTime,
                isNew,
                user.id.userId,
                user.name,
                user.avatar,
                comment.shortText,
                comment.imageUrl,
                commentContentId = comment.contentId.mapToContentId(),
                parentCommentContentId = parents.comment?.mapToContentId(),
                parentPostContentId = parents.post.mapToContentId())
        }
    }
}
