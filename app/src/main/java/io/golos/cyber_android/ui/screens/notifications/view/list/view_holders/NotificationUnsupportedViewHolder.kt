package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UnsupportedNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase

class NotificationUnsupportedViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<UnsupportedNotificationItem>(
    parentView,
    R.layout.item_notification_unsupported
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<UnsupportedNotificationItem>? = null
}