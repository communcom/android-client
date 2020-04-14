package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UpVoteNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedUpVote

class NotificationUpVoteViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<UpVoteNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<UpVoteNotificationItem>? =
        NotificationContentRenderedUpVote(viewDescription)
}