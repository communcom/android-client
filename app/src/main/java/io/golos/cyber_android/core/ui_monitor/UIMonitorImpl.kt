package io.golos.cyber_android.core.ui_monitor

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.core.feedback_service.FeedbackService
import io.golos.domain.LogTags
import javax.inject.Inject

class UIMonitorImpl
@Inject
constructor(private val appContext: Context): UIMonitor, Application.ActivityLifecycleCallbacks {

    private var activeActivities = 0

    init {
        (appContext as Application).registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(activity: Activity?) {
        activeActivities--

        if(activeActivities == 0) {
            FeedbackService.stop(appContext)
        }
    }

    override fun onActivityResumed(activity: Activity?) {
        activity?.javaClass?.simpleName?.let { App.logger.log(LogTags.NAVIGATION, "$it activity is active") }

        activeActivities++

        if(activeActivities > 0) {
            FeedbackService.start(appContext)
        }
    }

    override fun onActivityStarted(activity: Activity?) {
        // do nothing so far
    }

    override fun onActivityDestroyed(activity: Activity?) {
        // do nothing so far
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // do nothing so far
    }

    override fun onActivityStopped(activity: Activity?) {
        // do nothing so far
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        // do nothing so far
    }
}