package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationReplyItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor

class NotificationReplyViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationReplyItem>(
    parentView
) {

    override val notificationTypeLabelResId: Int = R.drawable.ic_reply_label

    override fun init(listItem: NotificationReplyItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
    }
}