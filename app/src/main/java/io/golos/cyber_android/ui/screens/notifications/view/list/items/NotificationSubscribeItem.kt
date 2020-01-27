package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.util.*


data class NotificationSubscribeItem(override val version: Long,
                                     override val id: Long,
                                     override val notificationId: String,
                                     override val createTime: Date,
                                     override val lastNotificationTime: String,
                                     val isNew: Boolean,
                                     val userId: String,
                                     val userName: String?,
                                     val userAvatar: String?): BaseNotificationItem(version, id, notificationId, createTime, lastNotificationTime), VersionedListItem