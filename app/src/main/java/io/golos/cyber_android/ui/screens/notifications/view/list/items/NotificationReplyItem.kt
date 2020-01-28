package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.util.*

data class NotificationReplyItem(override val version: Long,
                                 override val id: Long,
                                 override val notificationId: String,
                                 override val createTime: Date,
                                 override val isNew: Boolean,
                                 override val userId: String,
                                 override val userName: String?,
                                 override val userAvatar: String?
) :
    BaseNotificationItem(
        version,
        id,
        notificationId,
        createTime,
        isNew,
        userId,
        userName,
        userAvatar
    ), VersionedListItem