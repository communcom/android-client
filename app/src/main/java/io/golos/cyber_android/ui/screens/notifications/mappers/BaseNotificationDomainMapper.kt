package io.golos.cyber_android.ui.screens.notifications.mappers

import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationMentionItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationReplyItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationSubscribeItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationUpVoteItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.*
import io.golos.domain.utils.IdUtil

fun NotificationDomain.mapToVersionedListItem(): VersionedListItem {
    return when(this){
        is MentionNotificationDomain -> NotificationMentionItem(0, IdUtil.generateLongId(), id, createTime, isNew, user.id.userId, user.name, user.avatar)
        is SubscribeNotificationDomain -> NotificationSubscribeItem(0, IdUtil.generateLongId(), id, createTime, isNew, user.id.userId, user.name, user.avatar)
        is UpVoteNotificationDomain -> NotificationUpVoteItem(0, IdUtil.generateLongId(), id, createTime, isNew, user.id.userId, user.name, user.avatar)
        is ReplyNotificationDomain -> NotificationReplyItem(0, IdUtil.generateLongId(), id, createTime, isNew, user.id.userId, user.name, user.avatar)
    }
}
