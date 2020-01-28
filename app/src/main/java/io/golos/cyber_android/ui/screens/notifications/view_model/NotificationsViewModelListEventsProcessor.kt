package io.golos.cyber_android.ui.screens.notifications.view_model

import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.shared.recycler_view.BaseListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.SupportRetryListItemEventsProcessor

interface NotificationsViewModelListEventsProcessor: BaseListItemEventsProcessor, SupportRetryListItemEventsProcessor {

    fun loadMoreNotifications()

    fun onChangeFollowerStatusClicked(notification: BaseNotificationItem)

    fun onUserClicked(userId: String)
}