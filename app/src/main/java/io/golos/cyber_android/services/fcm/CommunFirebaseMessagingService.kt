package io.golos.cyber_android.services.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import kotlin.random.Random


class CommunFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val notificationBuilder = NotificationCompat.Builder(this, MAIN_CHANNEL_ID)
            .setContentTitle(message?.notification?.title)
            .setContentText(message?.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(defaultVibrationPattern)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(Random.Default.nextInt(), notificationBuilder.build())
    }

    companion object {
        const val MAIN_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.main_channel"
        private val defaultVibrationPattern = longArrayOf(100, 100, 100, 100)

        @RequiresApi(Build.VERSION_CODES.O)
        fun createChannels(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createChannel(
                notificationManager,
                MAIN_CHANNEL_ID,

                context.getString(R.string.notification_channel_main_title),
                context.getString(R.string.notification_channel_main_description)
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createChannel(
            manager: NotificationManager,
            id: String,
            title: String,
            description: String
        ) {
            val channel = NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH)
            channel.description = description
            channel.enableVibration(true)
            channel.vibrationPattern = defaultVibrationPattern
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder().build()
            )
            manager.createNotificationChannel(channel)
        }
    }
}