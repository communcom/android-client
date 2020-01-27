package io.golos.cyber_android.ui.screens.notifications.view.list.items

import java.util.*


data class NotificationMentionItem(override val version: Long,
                                   override val id: Long,
                                   override val notificationId: String,
                                   override val createTime: Date,
                                   override val lastNotificationTime: String): BaseNotificationItem(version, id, notificationId, createTime, lastNotificationTime)