package io.golos.cyber_android.ui.screens.login_activity.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_activity.dto.*
import io.golos.cyber_android.ui.screens.login_activity.view.animation.SplashAnimator
import io.golos.cyber_android.ui.screens.login_activity.view.animation.SplashAnimatorTarget
import io.golos.cyber_android.ui.screens.login_activity.view_model.LoginViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.base.ActivityBase
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.domain.utils.IdUtil
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : ActivityBase(), SplashAnimatorTarget {
    private lateinit var splashAnimator: SplashAnimator

    private enum class AuthStage {
        WELCOME,
        CONTINUE
    }

    private lateinit var viewModel: LoginViewModel

    private val injectionKey = IdUtil.generateStringId()

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var uiHelper: UIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<LoginActivityComponent>(injectionKey).inject(this)

        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[LoginViewModel::class.java]

        splashAnimator = SplashAnimator(this, viewModel.splashAnimationDuration)

        setupViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()

        splashAnimator.clear()

        if(isFinishing) {
            App.injections.release<LoginActivityComponent>(injectionKey)
        }
    }

    override fun getAnimatedView(): View = splashIcon

    override fun getRootView(): View = root

    private fun setupViewModel() {
        viewModel.command.observe(this, Observer {
            when(it) {
                is ShowMessageResCommand -> uiHelper.showMessage(it.textResId)

                is ShowSplashAnimationCommand -> splashAnimator.startAnimation(this)
                is HideSplashAnimationCommand -> splashAnimator.completeAnimation()

                is NavigateToWelcomeScreenCommand -> initAuthFlow(AuthStage.WELCOME)
                is NavigateToContinueSetupScreenCommand -> initAuthFlow(AuthStage.CONTINUE)
                is NavigateToMainScreenCommand -> navigateToMainScreen()
            }
        })
    }

    private fun initAuthFlow(stage: AuthStage) {
        val controller = postNavHost.findNavController()

        val inflater = controller.navInflater

        val graph = inflater.inflate(R.navigation.graph_login)
        graph.startDestination = when(stage) {
            AuthStage.WELCOME -> R.id.welcomeFragment
            AuthStage.CONTINUE -> R.id.signUpKeyFragment
        }

        controller.graph = graph
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
