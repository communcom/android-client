package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.foreground

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.domain.dto.UserIdDomain

class EventsProcessor : NotificationsViewModelListEventsProcessor {
    override fun loadMoreNotifications() { }

    override fun onChangeFollowerStatusClicked(notification: BaseNotificationItem) { }

    override fun onUserClickedById(userId: UserIdDomain) { }

    override fun onPostNavigateClicked(contentId: ContentId) { }

    override fun onWalletNavigateClicked() { }

    override fun onRetryLoadPage() { }
}