package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.SubscribeNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillSubscribe

class NotificationSubscribeViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<SubscribeNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<SubscribeNotificationItem> =
        NotificationViewFillSubscribe(viewDescription)
}