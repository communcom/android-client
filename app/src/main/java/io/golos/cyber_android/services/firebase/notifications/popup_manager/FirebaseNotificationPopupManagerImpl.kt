package io.golos.cyber_android.services.firebase.notifications.popup_manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.shared.ImageViewerActivity
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.dto.NotificationDomain
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class FirebaseNotificationPopupManagerImpl
@Inject
constructor(
    application: Application
) : FirebaseNotificationPopupManager,
    Application.ActivityLifecycleCallbacks {

    private var createdCount = 0
    private var activeCount = 0

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if(canProcessActivity(activity)) {
            createdCount++
        }
    }

    override fun onActivityStarted(activity: Activity?) { }

    override fun onActivityResumed(activity: Activity?) {
        if(canProcessActivity(activity)) {
            activeCount++
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
            createdCount <= 0 -> Timber.tag("FCM_MESSAGES").d("Show system popup")
            activeCount > 0 -> Timber.tag("FCM_MESSAGES").d("Show balloon")
        }
    }

    private fun canProcessActivity(activity: Activity?) =
        activity is EditorPageActivity || activity  is MainActivity || activity is ImageViewerActivity
}