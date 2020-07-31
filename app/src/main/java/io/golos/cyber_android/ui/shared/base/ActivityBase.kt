package io.golos.cyber_android.ui.shared.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.WelcomeActivity
import io.golos.domain.GlobalConstants
import io.golos.utils.id.IdUtil
import timber.log.Timber

abstract class ActivityBase : AppCompatActivity() {
    companion object {
        private const val INJECTION_KEY = "INJECTION_KEY"
        private const val APP_INSTANCE_ID_KEY = "APP_INSTANCE_ID_KEY"
    }

    private lateinit var injectionKey: String

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("676_BUG").d("Activity[${this::class.simpleName}]:onCreate() AppInstanceId current is: ${App.appInstanceId}")

        if(App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_DARK){
            setTheme(R.style.AppThemeDark)
            clearLightStatusBar()
        }else{
            setTheme(R.style.AppThemeLight)
            setLightStatusBar()
        }
        // Restart the application if its process has been killed
        if(savedInstanceState != null) {
            val appInstanceIdSaved = savedInstanceState.getString(APP_INSTANCE_ID_KEY)!!

            Timber.tag("676_BUG").d("Activity[${this::class.simpleName}]:onCreate() AppInstanceId loaded is: $appInstanceIdSaved")

            if(appInstanceIdSaved != App.appInstanceId) {
                val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                startActivity(welcomeIntent)
                finish()

                return
            }
        }

        super.onCreate(savedInstanceState)

        injectionKey = savedInstanceState?.getString(INJECTION_KEY) ?: IdUtil.generateStringId()
        inject(injectionKey)
    }

    fun setLightStatusBar() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
            }
        }
    }

    fun clearLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(INJECTION_KEY, injectionKey)
        outState.putString(APP_INSTANCE_ID_KEY, App.appInstanceId)

        Timber.tag("676_BUG").d("Activity[${this::class.simpleName}]:onSaveInstanceState() AppInstanceId saved is: ${App.appInstanceId}")

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if(isFinishing) {
            releaseInjection(injectionKey)
        }
        super.onDestroy()
    }

    protected abstract fun inject(key: String)

    protected abstract fun releaseInjection(key: String)
}