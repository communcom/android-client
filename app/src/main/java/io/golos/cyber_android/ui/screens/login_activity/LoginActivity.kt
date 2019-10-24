package io.golos.cyber_android.ui.screens.login_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.common.base.ActivityBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.screens.community_page.CommunityPageFragment
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimationManager
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimationManagerTarget
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimator
import io.golos.cyber_android.ui.screens.login_activity.animation.SplashAnimatorTarget
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.screens.profile.ProfileFragment
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsFragment
import io.golos.domain.requestmodel.SignInState
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : ActivityBase(), SplashAnimationManagerTarget, SplashAnimatorTarget {

    private val splashAnimationManager = SplashAnimationManager(this)
    private val splashAnimator = SplashAnimator(this)

    private enum class AuthStage {
        BEGINNING,
        PIN_CODE
    }
    private var authInProgress = false

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*App.injections.get<LoginActivityComponent>().inject(this)

        setContentView(R.layout.activity_login)
        setupViewModel()*/

        setContentView(R.layout.activity_login)
        supportFragmentManager.beginTransaction()
            .replace(R.id.testLayout, CommunityPageFragment.newInstance(""))
            .commit()
    }


    override fun onDestroy() {
        super.onDestroy()

        splashAnimationManager.clear()
        splashAnimator.clear()

        if(isFinishing) {
            App.injections.release<LoginActivityComponent>()
        }
    }

    private fun setupViewModel() {
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel::class.java)

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
