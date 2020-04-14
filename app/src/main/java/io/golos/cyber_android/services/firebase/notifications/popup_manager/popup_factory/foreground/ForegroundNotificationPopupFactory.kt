package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.foreground

import androidx.appcompat.app.AppCompatActivity
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.notifications.mappers.mapToVersionedListItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.fill_logic.*
import io.golos.domain.dto.NotificationDomain

class ForegroundNotificationPopupFactory {

    private var activeBalloon: Balloon? = null

    fun showNotification(notification: NotificationDomain, currentActivity: AppCompatActivity?) {
        if(currentActivity == null) {
            return
        }

        val listItem = notification.mapToVersionedListItem()

        if(listItem is UnsupportedNotificationItem) {
            return
        }

        activeBalloon?.dismiss()

        val balloon = Balloon.Builder(currentActivity)
            .setArrowVisible(false)
            .setLifecycleOwner(currentActivity)
            .setWidthRatio(0.98f)
            .setHeight(100)
            .setPadding(0)
            .setElevation(10f)
            .setAutoDismissDuration(5_000L)
            .setDismissWhenClicked(true)
            .setCornerRadius(11f)
            .setLayout(R.layout.item_notification)
            .setOnBalloonDismissListener { activeBalloon = null }
            .build()

        val contentView = balloon.getContentView()
        contentView.setBackgroundResource(R.drawable.bcg_thin_gray_stroke_ripple_6)

        fill(listItem, NotificationView(contentView), currentActivity, balloon)

        balloon.showAlignTop(currentActivity.findViewById(android.R.id.content))

        activeBalloon = balloon
    }

    private fun fill(
        notification: BaseNotificationItem,
        viewDescription: NotificationView,
        currentActivity: AppCompatActivity,
        balloon: Balloon) {

        val eventsProcessor = ForegroundNotificationsEventsProcessor(currentActivity, balloon)

        when (notification) {
            is TransferNotificationItem -> NotificationViewFillTransfer(viewDescription).init(notification, eventsProcessor)
            is RewardNotificationItem -> NotificationViewFillReward(viewDescription).init(notification, eventsProcessor)
            is MentionNotificationItem -> NotificationViewFillMention(viewDescription).init(notification, eventsProcessor)
            is SubscribeNotificationItem -> NotificationViewFillSubscribe(viewDescription).init(notification, eventsProcessor)
            is UpVoteNotificationItem -> NotificationViewFillUpVote(viewDescription).init(notification, eventsProcessor)
            is ReplyNotificationItem -> NotificationViewFillReply(viewDescription).init(notification, eventsProcessor)
            else -> throw UnsupportedOperationException("This notification is not supported")
        }
    }
}