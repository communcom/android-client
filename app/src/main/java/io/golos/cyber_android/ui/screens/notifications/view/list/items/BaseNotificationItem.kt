package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.util.*

open class BaseNotificationItem (
    override val version: Long,
    override val id: Long,

    override val isFirstItem: Boolean,
    override val isLastItem: Boolean,

    open val notificationId: String,
    open val createTime: Date,
    open val isNew: Boolean,
    open val userId: String,
    open val userName: String?,
    open val userAvatar: String?
): VersionedListItem