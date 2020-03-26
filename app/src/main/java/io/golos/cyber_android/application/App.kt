package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import io.golos.cyber_android.application.di_storage.DependencyInjectionStorage
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.application.shared.ui_monitor.UIMonitor
//import io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers.FacebookAuthProvider
import io.golos.domain.LogTags
import io.golos.utils.id.IdUtil
import timber.log.Timber
import javax.inject.Inject

class App : Application() {
    @Suppress("PropertyName")
    @Inject
    internal lateinit var timberTree: Timber.Tree

    @Inject
    internal lateinit var uiMonitor: UIMonitor

    @Inject
    internal lateinit var analitics: AnalyticsFacade

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

        analitics.init()

        //FacebookAuthProvider.printKeyHash(this)
    }
}