package io.golos.cyber_android.ui.screens.notifications.view.list.items

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import java.util.*


data class NotificationSubscribeItem(override val version: Long,
                                     override val id: Long,
                                     val notificationId: String,
                                     val isNew: Boolean,
                                     val createTime: Date,
                                     val lastNotificationsTime: String,
                                     val userId: String,
                                     val userName: String,
                                     val userAvatar: String): VersionedListItem