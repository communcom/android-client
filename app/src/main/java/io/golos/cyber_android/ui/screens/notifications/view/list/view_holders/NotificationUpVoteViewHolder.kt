package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UpVoteNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillUpVote

class NotificationUpVoteViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<UpVoteNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<UpVoteNotificationItem>? =
        NotificationViewFillUpVote(viewDescription)
}