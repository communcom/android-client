package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.golos.cyber_android.R

class NotificationViewLayout(override val root: View):
    NotificationView {
    override val messageText: TextView by lazy { root.findViewById<TextView>(R.id.tvMessage) }
    override val creationTimeText: TextView by lazy { root.findViewById<TextView>(R.id.tvCreateTime) }

    override val userIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivUserAvatar) }
    override val contentIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivContent) }
    override val notificationTypeIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivNotificationTypeLabel) }
    override val unreadIndicatorIcon: ImageView by lazy { root.findViewById<ImageView>(R.id.ivUnreadIndicator) }

    override val followButton: FrameLayout by lazy { root.findViewById<FrameLayout>(R.id.flAction) }
}