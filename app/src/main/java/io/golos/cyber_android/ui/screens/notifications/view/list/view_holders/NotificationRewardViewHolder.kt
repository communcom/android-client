package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.RewardNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedReward
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView

class NotificationRewardViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<RewardNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<RewardNotificationItem> =
        NotificationContentRenderedReward(viewDescription)
}