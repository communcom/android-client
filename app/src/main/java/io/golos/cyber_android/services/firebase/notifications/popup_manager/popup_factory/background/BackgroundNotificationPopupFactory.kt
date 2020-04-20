package io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.NotificationPopupFactoryBase
import io.golos.cyber_android.ui.mappers.mapToContentId
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.WelcomeActivity
import io.golos.cyber_android.ui.screens.notifications.view.list.items.*
import io.golos.cyber_android.ui.screens.notifications.view.list.view_holders.content_rendering.view.NotificationView
import io.golos.cyber_android.ui.shared.utils.IntentConstants
import io.golos.domain.dto.notifications.NotificationDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.utils.id.IdUtil

class BackgroundNotificationPopupFactory(
    private val appContext: Context
) : NotificationPopupFactoryBase() {
    private companion object {
        const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.COMMUN_NOTIFICATIONS"
    }

    fun clearAll() = NotificationManagerCompat.from(appContext).cancelAll()

    fun showNotification(notificationData: NotificationDomain) {
        val listItem = mapNotification(notificationData)

        if(listItem is UnsupportedNotificationItem) {
            return
        }

        createNotificationChannel()

        val stubView = NotificationViewStub(appContext)
        renderView(listItem, stubView, BackgroundNotificationsEventsProcessor())

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash_icon)
            .setLargeIcon(getLargeIcon(stubView))
            .setContentText(stubView.messageText.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createClickAction(listItem))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext).notify(IdUtil.generateIntId(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.app_name)
            val descriptionText = appContext.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getLargeIcon(view: NotificationView): Bitmap {
        val mainDrawable = view.userIcon.drawable ?: return BitmapFactory.decodeResource(appContext.resources, R.drawable.ic_commun)
        val secondDrawable = view.notificationTypeIcon.drawable

        val mainBitmap = mainDrawable.toBitmap(60, 60)

        return if(secondDrawable == null) {
            mainBitmap
        } else {
            val secondBitmap = secondDrawable.toBitmap(30, 30)

            val combinedBitmap = Bitmap.createBitmap(mainBitmap.width, mainBitmap.height,  mainBitmap.config)
            val canvas = Canvas(combinedBitmap)
            canvas.drawBitmap(mainBitmap, 0f, 0f, null)
            canvas.drawBitmap(secondBitmap, 30f, 30f, null)
            combinedBitmap
        }
    }

    private fun createClickAction(notification: BaseNotificationItem) : PendingIntent {
        val intent = Intent(appContext, WelcomeActivity::class.java).apply {
            action = IntentConstants.ACTION_OPEN_NOTIFICATION
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

            when(notification)  {
                is TransferNotificationItem,
                is RewardNotificationItem -> putExtra(IntentConstants.WALLET, true)

                is MentionNotificationItem ->
                    putExtra(IntentConstants.POST_CONTENT_ID,
                        (notification.post?.contentId ?: notification.comment?.parents?.post)?.mapToContentId()!!)

                is UpVoteNotificationItem ->
                    putExtra(IntentConstants.POST_CONTENT_ID,
                        (notification.post?.contentId ?: notification.comment?.parents?.post)?.mapToContentId()!!)

                is ReplyNotificationItem ->
                    putExtra(IntentConstants.POST_CONTENT_ID, notification.comment.parents.post.mapToContentId())

                is SubscribeNotificationItem -> putExtra(IntentConstants.USER_ID, UserIdDomain(notification.userId))

                else -> throw UnsupportedOperationException("This type of notification is not supported")
            }
        }

        return PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}