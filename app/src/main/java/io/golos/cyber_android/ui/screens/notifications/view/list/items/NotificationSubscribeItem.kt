package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.util.*

data class NotificationSubscribeItem(
    override val version: Long,
    override val id: Long,
    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,
    override val notificationId: String,
    override val createTime: Date,
    override val isNew: Boolean,
    override val userId: String,
    override val userName: String?,
    override val userAvatar: String?
) : BaseNotificationItem(
        version,
        id,
        isFirstItem,
        isLastItem,
        notificationId,
        createTime,
        isNew,
        userId,
        userName,
        userAvatar
), VersionedListItem