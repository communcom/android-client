package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory

import io.golos.cyber_android.ui.screens.notifications.mappers.mapToVersionedListItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.domain.dto.notifications.NotificationDomain

abstract class NotificationPopupFactoryBase {
    protected fun mapNotification(notification: NotificationDomain): BaseNotificationItem = notification.mapToVersionedListItem()

    protected fun renderView(
        notification: BaseNotificationItem,
        viewDescription: NotificationView,
        eventsProcessor: NotificationsViewModelListEventsProcessor) {

        when (notification) {
            is TransferNotificationItem -> NotificationContentRenderedTransfer(viewDescription).init(notification, eventsProcessor)
            is RewardNotificationItem -> NotificationContentRenderedReward(viewDescription).init(notification, eventsProcessor)
            is MentionNotificationItem -> NotificationContentRenderedMention(viewDescription).init(notification, eventsProcessor)
            is SubscribeNotificationItem -> NotificationContentRenderedSubscribe(viewDescription).init(notification, eventsProcessor)
            is UpVoteNotificationItem -> NotificationContentRenderedUpVote(viewDescription).init(notification, eventsProcessor)
            is ReplyNotificationItem -> NotificationContentRenderedReply(viewDescription).init(notification, eventsProcessor)
            else -> throw UnsupportedOperationException("This notification is not supported")
        }
    }
}