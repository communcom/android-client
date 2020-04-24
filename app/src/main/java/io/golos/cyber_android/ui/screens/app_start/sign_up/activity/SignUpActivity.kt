package io.golos.cyber_android.ui.screens.app_start.sign_up.activity

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_up.activity.di.SignUpActivityComponent
import io.golos.cyber_android.ui.shared.base.ActivityBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.SignUpCoreManagement
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SignUpActivity : ActivityBase(), CoroutineScope {
    private val scopeJob: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    @Inject
    internal lateinit var signUpCore: SignUpCoreManagement

    @Inject
    internal lateinit var keyValueStorage: KeyValueStorageFacade

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        overridePendingTransition(R.anim.nav_slide_in_right, R.anim.nav_slide_out_left)

        initSignUp()
    }

    override fun inject(key: String) = App.injections.get<SignUpActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpActivityComponent>(key)

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing) {
            signUpCore.close()
            scopeJob.cancel()
        }
    }

    private fun initSignUp() {
        launch {
            val signUpSnapshot = keyValueStorage.getSignUpSnapshot() ?: getDefaultSnapshot()

            val screenResId = when(signUpSnapshot.state) {
                SignUpState.SELECTING_SIGN_UP_METHOD -> R.id.signUpSelectMethodFragment
                SignUpState.WAITING_FOR_PHONE -> R.id.signUpPhoneFragment2
                SignUpState.WAITING_FOR_EMAIL -> R.id.signUpEmailFragment
                SignUpState.PHONE_VERIFICATION -> R.id.signUpVerificationFragment2
                SignUpState.EMAIL_VERIFICATION -> R.id.signUpEmailVerificationFragment
                SignUpState.ENTERING_USER_NAME -> R.id.signUpNameFragment2
                SignUpState.ENTERING_PASSWORD -> R.id.signUpCreatePasswordFragment2
                SignUpState.PASSWORD_CONFIRMATION -> R.id.signUpConfirmPasswordFragment2
                SignUpState.ENTERING_PIN -> R.id.signUpPinCodeFragment2
                SignUpState.SELECTING_METHOD_TO_UNLOCK -> R.id.signUpAppUnlockFragment2
                SignUpState.WAITING_FOR_GOOGLE_TOKEN, SignUpState.WAITING_FOR_FB_TOKEN, SignUpState.FINAL -> null
                else -> throw UnsupportedOperationException("This state is not supported: ${signUpSnapshot.state}")
            }

            if(screenResId == null) {
                signUpCore.init(getDefaultSnapshot())
                setStartScreen(R.id.signUpSelectMethodFragment)
            } else {
                signUpCore.init(signUpSnapshot)
                setStartScreen(screenResId)
            }
        }
    }

    private fun getDefaultSnapshot() = SignUpSnapshotDomain(
            state = SignUpState.SELECTING_SIGN_UP_METHOD,
            type = null,
            identity = null,
            phoneNumber = null,
            email = null,
            userName = null,
            userId = null,
            password = null,
            pinCode = null
        )

    private fun setStartScreen(@IdRes id: Int) {
        val controller = postNavHost.findNavController()

        val inflater = controller.navInflater

        val graph = inflater.inflate(R.navigation.graph_sign_up)
        graph.startDestination = id

        controller.graph = graph
    }
}
