package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationHeaderDateItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

class NotificationHeaderDateViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, NotificationHeaderDateItem>(
    parentView,
    R.layout.item_post_title
) {
    override fun init(listItem: NotificationHeaderDateItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {

    }
}