package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationViewLayout
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase

abstract class NotificationViewHolderBase<TItem: BaseNotificationItem>  (
    parentView: ViewGroup,
    @LayoutRes layoutResId: Int = R.layout.item_notification
) : ViewHolderBase<NotificationsViewModelListEventsProcessor, TItem>(
    parentView,
    layoutResId
) {
    private var contentRendered: NotificationContentRenderedBase<TItem>? = null

    override fun init(listItem: TItem, listItemEventsProcessor: NotificationsViewModelListEventsProcessor) {
        contentRendered = createContentRenderer(NotificationViewLayout(itemView)
        )
        contentRendered?.init(listItem, listItemEventsProcessor)
    }

    override fun release() {
        contentRendered?.release()
        super.release()
    }

    protected abstract fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<TItem>?
}