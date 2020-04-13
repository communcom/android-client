package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UnsupportedNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase

class NotificationUnsupportedViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<UnsupportedNotificationItem>(
    parentView,
    R.layout.item_notification_unsupported
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<UnsupportedNotificationItem>? = null
}