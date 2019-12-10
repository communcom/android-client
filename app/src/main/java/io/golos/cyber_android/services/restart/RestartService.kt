package io.golos.cyber_android.services.restart

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_activity.LoginActivity

/**
 * Restarting the application
 */
class RestartService : Service() {
    private val notificationId = 127353401

    override fun onCreate() {
        super.onCreate()

        Log.d("RESTART", "RestartService::onCreate()")

        startForeground(notificationId, createSyncServiceNotification())
        startLaunchActivity()
//        stopForeground(true)
//        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun startLaunchActivity() {
        val startActivity = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(startActivity)

//        baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
//            ?.let { intent ->
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
    }

    /** Create notification for Comet service
     * @return Notification and its id */
    @Suppress("BooleanLiteralArgument")
    @SuppressLint("InlinedApi")
    fun createSyncServiceNotification(): Notification {
        return if(Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(resources.getString(R.string.app_name), "Some text", NotificationManager.IMPORTANCE_LOW)

            channel.enableLights(false)
            channel.enableVibration(false)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)

            NotificationCompat.Builder(this, channel.id)
                .setContentText("")
                .setContentTitle("")
                .build()
        }
        else {
            throw UnsupportedOperationException("Method requires API level 26")
        }

    }
}
