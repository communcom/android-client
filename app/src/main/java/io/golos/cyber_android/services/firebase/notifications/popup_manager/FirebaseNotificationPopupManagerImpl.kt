package io.golos.cyber_android.services.firebase.notifications.popup_manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.background.BackgroundNotificationPopupFactory
import io.golos.cyber_android.services.firebase.notifications.popup_manager.popup_factory.foreground.ForegroundNotificationPopupFactory
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.shared.ImageViewerActivity
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.NotificationDomain
import java.lang.ref.WeakReference
import javax.inject.Inject

@ApplicationScope
class FirebaseNotificationPopupManagerImpl
@Inject
constructor(
    application: Application,
    private val appContext: Context
) : FirebaseNotificationPopupManager,
    Application.ActivityLifecycleCallbacks {

    private var createdCount = 0
    private var activeCount = 0

    private var currentActivity: WeakReference<Activity>? = null

    private val backgroundNotificationsFactory
        get() = BackgroundNotificationPopupFactory(appContext)

    private val foregroundNotificationsFactory
        get() = ForegroundNotificationPopupFactory()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if(canProcessActivity(activity)) {
            createdCount++
        }
    }

    override fun onActivityStarted(activity: Activity?) { }

    override fun onActivityResumed(activity: Activity) {
        if(canProcessActivity(activity)) {
            activeCount++
            currentActivity = WeakReference(activity)

            backgroundNotificationsFactory.clearAll()
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        if(canProcessActivity(activity)) {
            activeCount--
        }
    }

    override fun onActivityStopped(activity: Activity?) { }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) { }

    override fun onActivityDestroyed(activity: Activity?) {
        if(canProcessActivity(activity)) {
            createdCount--
        }
    }

    override fun showNotification(notification: NotificationDomain) {
        when {
            createdCount <= 0 || (createdCount > 0 && activeCount == 0) ->
                backgroundNotificationsFactory.showNotification(notification)

            activeCount > 0 ->
                foregroundNotificationsFactory.showNotification(notification, currentActivity?.get() as? AppCompatActivity)
        }
    }

    private fun canProcessActivity(activity: Activity?) =
        activity is EditorPageActivity || activity  is MainActivity || activity is ImageViewerActivity
}