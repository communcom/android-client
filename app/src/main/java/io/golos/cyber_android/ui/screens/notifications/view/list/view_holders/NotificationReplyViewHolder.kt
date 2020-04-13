package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReplyNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillReply

class NotificationReplyViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<ReplyNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<ReplyNotificationItem> =
        NotificationViewFillReply(viewDescription)
}