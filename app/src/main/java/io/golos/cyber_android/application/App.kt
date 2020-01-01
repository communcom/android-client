package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import io.golos.cyber_android.di.DependencyInjectionStorage
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.core.ui_monitor.UIMonitor
import io.golos.domain.LogTags
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class App : Application() {
    @Inject
    internal lateinit var appCore: AppCore

    @Suppress("PropertyName")
    @Inject
    internal lateinit var timberTree: Timber.Tree

    @Inject
    internal lateinit var uiMonitor: UIMonitor

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var injections : DependencyInjectionStorage
            private set
    }

    override fun onCreate() {
        super.onCreate()

        injections = DependencyInjectionStorage(applicationContext)
        injections.get<AppComponent>().inject(this)

        Timber.plant(timberTree)
        Timber.tag(LogTags.NAVIGATION).d("The app is started")

        appCore.initialize()
    }
}