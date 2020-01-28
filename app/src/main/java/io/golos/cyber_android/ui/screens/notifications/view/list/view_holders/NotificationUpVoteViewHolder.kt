package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationUpVoteItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor

class NotificationUpVoteViewHolder(
    parentView: ViewGroup
) : BaseNotificationViewHolder<NotificationUpVoteItem>(
    parentView
) {
    override val notificationTypeLabelResId: Int = R.drawable.ic_up_vote_label

    override fun init(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        super.init(listItem, listItemEventsProcessor)
    }
}