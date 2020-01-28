package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationMentionItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor

class NotificationMentionViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationMentionItem>(
    parentView
) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_mention_label

    override fun init(listItem: NotificationMentionItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
    }
}