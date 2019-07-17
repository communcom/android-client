package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.application.dependency_injection.DependencyInjectionStorage
import io.golos.cyber_android.fcm.CommunFirebaseMessagingService
import io.golos.cyber_android.application.locator.RepositoriesHolder
import io.golos.cyber_android.application.locator.ServiceLocator
import io.golos.cyber_android.application.locator.ServiceLocatorImpl

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class App : Application() {
    private lateinit var mServiceLocator: ServiceLocator
    private lateinit var mRepositoriesHolder: RepositoriesHolder
    private lateinit var appCore: AppCore

    companion object {
        @JvmStatic
        val SERVICE_LOCATOR = "service_locator"

        @SuppressLint("StaticFieldLeak")
        lateinit var injections : DependencyInjectionStorage
            private set
    }

    override fun onCreate() {
        super.onCreate()

        injections = DependencyInjectionStorage(applicationContext)
//        injections.get<AppComponent>().inject(this)

        if (!BuildConfig.DEBUG)
            Fabric.with(this, Crashlytics())

        mServiceLocator = ServiceLocatorImpl(this)
        mRepositoriesHolder = mServiceLocator as RepositoriesHolder

        appCore = AppCore(
            mRepositoriesHolder,
            mServiceLocator.dispatchersProvider
        )
        appCore.initialize()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CommunFirebaseMessagingService.createChannels(this)
        }
    }

    override fun getSystemService(name: String): Any? {
        if (name == SERVICE_LOCATOR) return mServiceLocator
        return super.getSystemService(name)
    }
}