package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.background

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView

class NotificationViewStub(private val appContext: Context) : NotificationView {
    override val root by lazy { View(appContext) }

    override val messageText by lazy { TextView(appContext) }

    override val creationTimeText by lazy { TextView(appContext) }

    override val userIcon by lazy { ImageView(appContext) }

    override val contentIcon by lazy { ImageView(appContext) }

    override val notificationTypeIcon by lazy { ImageView(appContext) }

    override val unreadIndicatorIcon by lazy { ImageView(appContext) }

    override val followButton by lazy { FrameLayout(appContext) }
}