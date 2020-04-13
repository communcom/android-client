package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.application.di_storage.DependencyInjectionStorage
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.services.firebase.notifications.popup_manager.FirebaseNotificationPopupManager
//import io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers.FacebookAuthProvider
import io.golos.domain.LogTags
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.utils.id.IdUtil
import timber.log.Timber
import zerobranch.androidremotedebugger.AndroidRemoteDebugger
import javax.inject.Inject

class App : Application() {
    @Suppress("PropertyName")
    @Inject
    internal lateinit var timberTree: Timber.Tree

    @Inject
    internal lateinit var analytics: AnalyticsFacade

    @Inject
    internal lateinit var firebaseNotificationsManager: FirebaseNotificationPopupManager

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var injections : DependencyInjectionStorage
            private set
    }

    override fun onCreate() {
        super.onCreate()

        injections = DependencyInjectionStorage(this)
        injections.get<AppComponent>(IdUtil.generateStringId()).inject(this)

        Timber.plant(timberTree)
        Timber.tag(LogTags.NAVIGATION).d("The app is started")

        initRemoteLogging()

        analytics.init()

        //FacebookAuthProvider.printKeyHash(this)
    }

    @Suppress("ConstantConditionIf")
    private fun initRemoteLogging() {
        if(BuildConfig.FLAVOR == "checking") {
            AndroidRemoteDebugger.init(applicationContext)
        }
    }
}