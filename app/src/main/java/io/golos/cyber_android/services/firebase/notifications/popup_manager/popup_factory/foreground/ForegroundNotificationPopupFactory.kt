package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.foreground

import androidx.appcompat.app.AppCompatActivity
import com.skydoves.balloon.Balloon
import io.golos.cyber_android.R
import io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.NotificationPopupFactoryBase
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.UnsupportedNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationViewLayout
import io.golos.domain.dto.NotificationDomain

class ForegroundNotificationPopupFactory: NotificationPopupFactoryBase() {

    private var activeBalloon: Balloon? = null

    fun showNotification(notification: NotificationDomain, currentActivity: AppCompatActivity?) {
        if(currentActivity == null) {
            return
        }

        val listItem = mapNotification(notification)

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

        fill(listItem, NotificationViewLayout(contentView), currentActivity, balloon)

        balloon.showAlignTop(currentActivity.findViewById(android.R.id.content))

        activeBalloon = balloon
    }

    private fun fill(
        notification: BaseNotificationItem,
        viewDescription: NotificationView,
        currentActivity: AppCompatActivity,
        balloon: Balloon) {

        val eventsProcessor = ForegroundNotificationsEventsProcessor(currentActivity, balloon)
        renderView(notification, viewDescription, eventsProcessor)
    }
}