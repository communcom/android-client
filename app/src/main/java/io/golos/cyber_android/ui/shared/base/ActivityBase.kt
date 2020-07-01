package io.golos.cyber_android.ui.shared.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.WelcomeActivity
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