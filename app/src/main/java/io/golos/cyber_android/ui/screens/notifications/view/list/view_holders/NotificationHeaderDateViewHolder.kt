package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationHeaderDateItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import kotlinx.android.synthetic.main.item_notification_header_date.view.*

class NotificationHeaderDateViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, NotificationHeaderDateItem>(
    parentView,
    R.layout.item_notification_header_date
) {
    override fun init(listItem: NotificationHeaderDateItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        itemView.tvDateLabel.text = listItem.dateLabel
    }
}