package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.RewardNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillReward

class NotificationRewardViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<RewardNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<RewardNotificationItem> =
        NotificationViewFillReward(viewDescription)
}