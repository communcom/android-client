package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReferralRegistrationBonusNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRendererReferralRegistrationBonus
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView

class NotificationReferralRegistrationBonusViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<ReferralRegistrationBonusNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<ReferralRegistrationBonusNotificationItem> =
        NotificationContentRendererReferralRegistrationBonus(viewDescription)
}