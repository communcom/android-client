package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.EmptyStubNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_notification_empty_stub.view.*

class NotificationEmptyStubViewHolder (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, EmptyStubNotificationItem>(
    parentView,
    R.layout.item_notification_empty_stub
) {
    override fun init(listItem: EmptyStubNotificationItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        itemView.commentEmptyHolder.setTitle(R.string.no_notifications)
        itemView.commentEmptyHolder.setExplanation(R.string.no_notifications_explanation)
    }
}