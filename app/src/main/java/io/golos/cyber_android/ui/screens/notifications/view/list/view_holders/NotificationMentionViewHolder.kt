package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.MentionNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedMention

class NotificationMentionViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<MentionNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView) = NotificationContentRenderedMention(viewDescription)
}