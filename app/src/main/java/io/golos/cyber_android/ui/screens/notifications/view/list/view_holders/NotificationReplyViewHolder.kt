package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationReplyItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

class NotificationReplyViewHolder (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, NotificationReplyItem>(
    parentView,
    R.layout.item_notification
) {
    override fun init(listItem: NotificationReplyItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {

    }
}