package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.di_storage.DependencyInjectionStorage
import io.golos.cyber_android.application.di.AppComponent
import io.golos.cyber_android.services.firebase.notifications.popup_manager.FirebaseNotificationPopupManager
import io.golos.domain.GlobalConstants
import io.golos.domain.KeyValueStorageFacade
//import io.golos.cyber_android.ui.screens.login_sign_up_select_method.social_network_auth_providers.FacebookAuthProvider
import io.golos.domain.LogTags
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.utils.id.IdUtil
import timber.log.Timber
import zerobranch.androidremotedebugger.AndroidRemoteDebugger
import javax.inject.Inject
import io.golos.cyber_android.application.shared.logger.ClickableTree

class App : Application() {
    @Suppress("PropertyName")
    @Inject
    internal lateinit var timberTree: Timber.Tree

    @Inject
    internal lateinit var analytics: AnalyticsFacade

    @Inject
    internal lateinit var keyValueStorage: KeyValueStorageFacade

    @Inject
    internal lateinit var firebaseNotificationsManager: FirebaseNotificationPopupManager

    companion object {
        lateinit var mInstance:App
        @SuppressLint("StaticFieldLeak")
        lateinit var injections : DependencyInjectionStorage
            private set

        fun getInstance() = mInstance

        val appInstanceId = IdUtil.generateStringId()

    }

    override fun onCreate() {
        super.onCreate()

        mInstance = this

        val key = IdUtil.generateStringId()
        injections = DependencyInjectionStorage(this)
        injections.get<AppComponent>(key).inject(this)

        Timber.plant(timberTree)
        Timber.plant(ClickableTree())
        Timber.tag(LogTags.NAVIGATION).d("The app is started")

        Timber.tag("676_BUG").d("The app is started. InstanceId is: $appInstanceId")

        initRemoteLogging()

        analytics.init()

        when(keyValueStorage.getUIMode()){
            null -> {
                if(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                    setTheme(R.style.AppThemeDark)
                }else{
                    setTheme(R.style.AppThemeLight)
                }
                keyValueStorage.setUIMode(GlobalConstants.UI_MODE_UNSPECIFIED)
            }
            GlobalConstants.UI_MODE_DARK ->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.AppThemeDark)
            }
            GlobalConstants.UI_MODE_LIGHT ->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.AppThemeLight)
            }
        }

        //FacebookAuthProvider.printKeyHash(this)
    }

    @Suppress("ConstantConditionIf")
    private fun initRemoteLogging() {
        if(BuildConfig.FLAVOR == "checking") {
            AndroidRemoteDebugger.init(applicationContext)
        }
    }
}