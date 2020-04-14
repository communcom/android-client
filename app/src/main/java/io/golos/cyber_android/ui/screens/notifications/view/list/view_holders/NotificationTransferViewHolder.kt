package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.TransferNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedTransfer

class NotificationTransferViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<TransferNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<TransferNotificationItem> =
        NotificationContentRenderedTransfer(viewDescription)
}