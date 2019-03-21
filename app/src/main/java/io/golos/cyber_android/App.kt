package io.golos.cyber_android

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.golos.cyber_android.locator.ServiceLocator
import io.golos.cyber_android.locator.ServiceLocatorImpl


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class App : MultiDexApplication() {
    private lateinit var mServiceLocator: ServiceLocator


    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG)
            Fabric.with(this, Crashlytics())

        mServiceLocator = ServiceLocatorImpl(this)
    }

    override fun getSystemService(name: String): Any {
        if (name == SERVICE_LOCATOR) return mServiceLocator
        return super.getSystemService(name)
    }

    companion object {
        @JvmStatic
        val SERVICE_LOCATOR = "service_locator"
    }
}