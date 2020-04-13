package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.MentionNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillMention

class NotificationMentionViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<MentionNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView) = NotificationViewFillMention(viewDescription)
}