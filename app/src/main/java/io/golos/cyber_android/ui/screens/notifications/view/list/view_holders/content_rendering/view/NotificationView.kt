package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

interface NotificationView {
    val root: View
    val messageText: TextView
    val creationTimeText: TextView

    val userIcon: ImageView
    val contentIcon: ImageView
    val notificationTypeIcon: ImageView
    val unreadIndicatorIcon: ImageView

    val followButton: FrameLayout
}