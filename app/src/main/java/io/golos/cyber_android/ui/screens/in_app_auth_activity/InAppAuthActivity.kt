package io.golos.cyber_android.ui.screens.in_app_auth_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.in_app_auth_activity.di.InAppAuthActivityComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.Navigator
import io.golos.cyber_android.ui.shared.base.ActivityBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.fingerprint.FingerprintAuthManager
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class InAppAuthActivity : ActivityBase(), CoroutineScope {
    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var keyValueStorage: KeyValueStorageFacade

    @Inject
    lateinit var fingerprintAuthManager: FingerprintAuthManager

    @Inject
    lateinit var dispatchersProvider: DispatchersProvider

    companion object {
        const val REQUEST_CODE = 4263

        const val PIN_CODE_HEADER_ID = "PIN_CODE_HEADER_ID"
        const val FINGERPRINT_HEADER_ID = "FINGERPRINT_CODE_HEADER_ID"
        const val PIN_CODE_UNLOCK_ENABLED = "PIN_CODE_UNLOCK_ENABLED"

        fun start(activity: ActivityBase,
                  isPinCodeUnlockEnabled: Boolean = true,
                  @StringRes pinCodeHeaderText: Int = R.string.authPinCodeDefaultHeader,
                  @StringRes fingerprintHeaderText: Int = R.string.authFingerprintDefaultHeader) {

            activity.startActivityForResult(createStartIntent(activity, isPinCodeUnlockEnabled, pinCodeHeaderText, fingerprintHeaderText), REQUEST_CODE)
        }

        fun start(fragment: Fragment,
                  isPinCodeUnlockEnabled: Boolean = true,
                  @StringRes pinCodeHeaderText: Int = R.string.authPinCodeDefaultHeader,
                  @StringRes fingerprintHeaderText: Int = R.string.authFingerprintDefaultHeader) {

            fragment.startActivityForResult(createStartIntent(fragment.requireContext(), isPinCodeUnlockEnabled, pinCodeHeaderText, fingerprintHeaderText), REQUEST_CODE)
        }

        private fun createStartIntent(context: Context,
                                      isPinCodeUnlockEnabled: Boolean = true,
                                      @StringRes pinCodeHeaderText: Int,
                                      @StringRes fingerprintHeaderText: Int): Intent =
            Intent(context, InAppAuthActivity::class.java)
                .also { intent ->
                    Bundle()
                        .apply {
                            putInt(PIN_CODE_HEADER_ID, pinCodeHeaderText)
                            putInt(FINGERPRINT_HEADER_ID, fingerprintHeaderText)
                            putBoolean(PIN_CODE_UNLOCK_ENABLED, isPinCodeUnlockEnabled)
                            intent.putExtras(this)
                        }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_auth)

        launch {
            val isPinCodeEnabled = intent.extras!!.getBoolean(PIN_CODE_UNLOCK_ENABLED)

            if(isFingerpringUnlockPossible() && !isPinCodeEnabled) {
                navigator.setFingerprintAsHome(this@InAppAuthActivity,
                    intent.extras!!.getInt(FINGERPRINT_HEADER_ID),
                    isPinCodeEnabled)
            } else {
                when(getAppUnlockWay()) {
                    AppUnlockWay.PIN_CODE ->
                        navigator.setPinCodeAsHome(this@InAppAuthActivity, intent.extras!!.getInt(PIN_CODE_HEADER_ID))
                    AppUnlockWay.FINGERPRINT ->
                        navigator.setFingerprintAsHome(this@InAppAuthActivity,
                            intent.extras!!.getInt(FINGERPRINT_HEADER_ID),
                            isPinCodeEnabled)
                }
            }
        }
    }

    override fun onBackPressed() {
        navigator.setAuthFailResult(this)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isFinishing) {
            scopeJob.takeIf { it.isActive }?.cancel()
        }
    }

    override fun inject(key: String) = App.injections.get<InAppAuthActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<InAppAuthActivityComponent>(key)

    private suspend fun getAppUnlockWay(): AppUnlockWay =
        withContext(dispatchersProvider.ioDispatcher) {
            if(!fingerprintAuthManager.isAuthenticationPossible) {
                return@withContext AppUnlockWay.PIN_CODE
            }

            keyValueStorage.getAppUnlockWay() ?: AppUnlockWay.PIN_CODE
        }

    private suspend fun isFingerpringUnlockPossible(): Boolean =
        withContext(dispatchersProvider.ioDispatcher) {
            fingerprintAuthManager.isAuthenticationPossible
        }
}