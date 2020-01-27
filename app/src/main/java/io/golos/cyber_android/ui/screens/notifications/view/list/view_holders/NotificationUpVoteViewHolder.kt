package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationUpVoteItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

class NotificationUpVoteViewHolder (
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, NotificationUpVoteItem>(
    parentView,
    R.layout.item_notification
) {
    override fun init(listItem: NotificationUpVoteItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {

    }
}