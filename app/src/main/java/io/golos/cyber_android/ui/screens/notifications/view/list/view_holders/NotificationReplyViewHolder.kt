package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReplyNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedReply

class NotificationReplyViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<ReplyNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<ReplyNotificationItem> =
        NotificationContentRenderedReply(viewDescription)
}