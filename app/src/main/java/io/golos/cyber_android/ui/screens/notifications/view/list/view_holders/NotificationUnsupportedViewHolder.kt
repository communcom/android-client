package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UnsupportedNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor

class NotificationUnsupportedViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<UnsupportedNotificationItem>(
    parentView,
    R.layout.item_notification_unsupported
) {
    override fun init(listItem: UnsupportedNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}