package io.golos.cyber_android.ui.screens.notifications.view_model

import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.shared.recycler_view.BaseListItemEventsProcessor
import io.golos.cyber_android.ui.shared.recycler_view.SupportRetryListItemEventsProcessor
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain

interface NotificationsViewModelListEventsProcessor: BaseListItemEventsProcessor, SupportRetryListItemEventsProcessor {

    fun loadMoreNotifications()

    fun onChangeFollowerStatusClicked(notification: BaseNotificationItem)

    fun onUserClickedById(userId: UserIdDomain)

    fun onPostNavigateClicked(contentId: ContentIdDomain)

    fun onWalletNavigateClicked()
}