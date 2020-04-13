package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.TransferNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillTransfer

class NotificationTransferViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<TransferNotificationItem>(
    parentView
) {
    override fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<TransferNotificationItem> =
        NotificationViewFillTransfer(viewDescription)
}