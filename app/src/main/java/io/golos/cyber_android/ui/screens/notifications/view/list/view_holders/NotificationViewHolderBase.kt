package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.NotificationViewFillBase
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

abstract class NotificationViewHolderBase<TItem: BaseNotificationItem>  (
    parentView: ViewGroup,
    @LayoutRes layoutResId: Int = R.layout.item_notification
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, TItem>(
    parentView,
    layoutResId
) {
    private var viewFill: NotificationViewFillBase<TItem>? = null

    override fun init(listItem: TItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        viewFill = createViewFill(NotificationView(itemView))
        viewFill?.init(listItem, listItemEventsProcessor)
    }

    override fun release() {
        viewFill?.release()
        super.release()
    }

    protected abstract fun createViewFill(viewDescription: NotificationView): NotificationViewFillBase<TItem>?
}