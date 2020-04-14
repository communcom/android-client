package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.background

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModelListEventsProcessor
import io.golos.domain.dto.UserIdDomain

class BackgroundNotificationsEventsProcessor : NotificationsViewModelListEventsProcessor {
    override fun loadMoreNotifications() {
        // do nothing
    }

    override fun onChangeFollowerStatusClicked(notification: BaseNotificationItem) {
        // do nothing
    }

    override fun onUserClickedById(userId: UserIdDomain) {
        // do nothing
    }

    override fun onPostNavigateClicked(contentId: ContentId) {
        // do nothing
    }

    override fun onWalletNavigateClicked() {
        // do nothing
    }

    override fun onRetryLoadPage() {
        // do nothing
    }
}