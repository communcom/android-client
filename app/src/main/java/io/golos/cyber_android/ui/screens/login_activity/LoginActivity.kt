package io.golos.cyber_android.ui.screens.login_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.ActivityBase
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.domain.requestmodel.SignInState
import kotlinx.android.synthetic.main.activity_login.*
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimationManagerTarget
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimationManager
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimator
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimatorTarget


class LoginActivity : ActivityBase(), SplashAnimationManagerTarget, SplashAnimatorTarget {

    private val splashAnimationManager = SplashAnimationManager(this)
    private val splashAnimator = SplashAnimator(this)

    private enum class AuthStage {
        BEGINNING,
        PIN_CODE
    }
    private var authInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setupViewModel()
    }


    override fun onDestroy() {
        super.onDestroy()

        splashAnimationManager.clear()
        splashAnimator.clear()
    }

    private fun setupViewModel() {
        val viewModel = ViewModelProviders.of(this, serviceLocator.getDefaultViewModelFactory()).get(AuthViewModel::class.java)

        viewModel.authLiveData.observe(this, Observer {
            splashAnimationManager.processEvent(it)

            when(it) {
                SignInState.LOG_IN_NEEDED ->
                    if(postNavHost.findNavController().currentDestination == null) {
                        splashAnimator.executeWhenCompleted {
                            initAuthFlow(AuthStage.BEGINNING)
                        }
                    }

                SignInState.LOADING -> splashAnimator.executeWhenCompleted { onLoading() }

                SignInState.USER_LOGGED_IN_SETUP_COMPLETED ->
                    if(postNavHost.findNavController().currentDestination == null) {
                        splashAnimator.executeWhenCompleted { navigateToMainScreen() }
                    }

                SignInState.USER_LOGGED_IN_SETUP_NOT_COMPLETED ->
                    if(postNavHost.findNavController().currentDestination == null) {
                        splashAnimator.executeWhenCompleted {
                            initAuthFlow(AuthStage.PIN_CODE)
                        }
                    }

                else -> {  }
            }
        })
    }

    private fun initAuthFlow(stage: AuthStage) {
        if(authInProgress) {
            return
        }

        authInProgress = true

        val controller = postNavHost.findNavController()

        val inflater = controller.navInflater

        val graph = inflater.inflate(R.navigation.graph_login)
        graph.startDestination = when(stage) {
            AuthStage.BEGINNING -> R.id.welcomeFragment
            AuthStage.PIN_CODE -> R.id.signUpKeyFragment
        }

        controller.graph = graph
    }

    private fun onLoading() {
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        splashAnimationManager.clear()
        finish()
    }

    override fun startSplashAnimation() = splashAnimator.startAnimation(this)

    override fun completeSplashAnimation() = splashAnimator.completeAnimation()

    override fun getAnimatedView(): View = splashIcon

    override fun getRootView(): View = root
}
