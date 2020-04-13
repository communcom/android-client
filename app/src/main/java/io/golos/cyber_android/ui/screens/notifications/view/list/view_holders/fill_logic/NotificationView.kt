package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.golos.cyber_android.R

class NotificationView(val root: View) {
    val messageText: TextView by lazy { root.findViewById<TextView>(R.id.tvMessage) }
    val creationTimeText: TextView by lazy { root.findViewById<TextView>(R.id.tvCreateTime) }

    val userIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivUserAvatar) }
    val contentIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivContent) }
    val notificationTypeIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivNotificationTypeLabel) }
    val unreadIndicatorIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivUnreadIndicator) }

    val followButton: FrameLayout by lazy { root.findViewById<FrameLayout>(R.id.flAction) }
}