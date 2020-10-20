package io.golos.cyber_android.ui.screens.notifications.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.ui.screens.notifications.view.list.items.ReferralPurchaseBonusNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRenderedBase
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.NotificationContentRendererReferralPurchaseBonus
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView

class NotificationReferralPurchaseBonusViewHolder(
    parentView: ViewGroup
) : NotificationViewHolderBase<ReferralPurchaseBonusNotificationItem>(
    parentView
) {
    override fun createContentRenderer(viewDescription: NotificationView): NotificationContentRenderedBase<ReferralPurchaseBonusNotificationItem> =
        NotificationContentRendererReferralPurchaseBonus(viewDescription)
}