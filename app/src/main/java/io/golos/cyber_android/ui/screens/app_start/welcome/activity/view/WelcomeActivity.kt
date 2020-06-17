package io.golos.cyber_android.ui.screens.app_start.welcome.activity.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.di.WelcomeActivityComponent
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.HideSplashAnimationCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.NavigateToWelcomeScreenCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.ShowSplashAnimationCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.animation.SplashAnimator
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view.animation.SplashAnimatorTarget
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.view_model.WelcomeViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.base.ActivityBase
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.popups.app_update.AppUpdatePopup
import io.golos.cyber_android.ui.shared.popups.no_connection.NoConnectionPopup
import io.golos.cyber_android.ui.shared.utils.IntentConstants
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.activity_welcome.*
import javax.inject.Inject

class WelcomeActivity : ActivityBase(), SplashAnimatorTarget {
    private lateinit var splashAnimator: SplashAnimator

    private lateinit var viewModel: WelcomeViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var uiHelper: UIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[WelcomeViewModel::class.java]

        splashAnimator = SplashAnimator(this, viewModel.splashAnimationDuration)

        setupViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()

        splashAnimator.clear()
    }

    override fun inject(key: String) = App.injections.get<WelcomeActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WelcomeActivityComponent>(key)

    override fun getAnimatedView(): View = splashIcon

    override fun getRootView(): View = root

    private fun setupViewModel() {
        viewModel.command.observe(this, Observer {
            when(it) {
                is ShowMessageResCommand -> uiHelper.showMessage(it.textResId, it.isError)
                is ShowMessageTextCommand -> uiHelper.showMessage(it.text, it.isError)

                is ShowSplashAnimationCommand -> splashAnimator.startAnimation(this)
                is HideSplashAnimationCommand -> splashAnimator.completeAnimation()

                is NavigateToWelcomeScreenCommand -> navigateToWelcome()
                is NavigateToMainScreenCommand -> navigateToMainScreen()
                is ShowNoConnectionDialogCommand -> NoConnectionPopup.show(this, root) { viewModel.processLogin() }
                is HideNoConnectionDialogCommand -> NoConnectionPopup.hide(this)

                is ShowUpdateAppDialogCommand -> AppUpdatePopup.show(this, root)
            }
        })
    }

    private fun navigateToWelcome() {
        val controller = postNavHost.findNavController()

        val inflater = controller.navInflater

        val graph = inflater.inflate(R.navigation.graph_login)
        graph.startDestination = R.id.welcomeFragment

        controller.graph = graph
    }

    private fun navigateToMainScreen() {
        val oldIntent = intent

        val startMainScreenIntent = Intent(this, MainActivity::class.java)
            .also { newIntent ->
                when(oldIntent.action) {
                    // Has deep link
                    Intent.ACTION_VIEW -> {
                        newIntent.action = oldIntent.action
                        newIntent.data = oldIntent.data
                    }

                    // Click on notification
                    IntentConstants.ACTION_OPEN_NOTIFICATION -> {
                        newIntent.action = oldIntent.action
                        newIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

                        if(oldIntent.extras!!.containsKey(IntentConstants.POST_CONTENT_ID)) {
                            newIntent.putExtra(IntentConstants.POST_CONTENT_ID, oldIntent.getParcelableExtra<ContentIdDomain>(IntentConstants.POST_CONTENT_ID))
                        }
                        if(oldIntent.extras!!.containsKey(IntentConstants.USER_ID)) {
                            newIntent.putExtra(IntentConstants.USER_ID, oldIntent.getParcelableExtra<UserIdDomain>(IntentConstants.USER_ID))
                        }
                        if(oldIntent.extras!!.containsKey(IntentConstants.WALLET)) {
                            newIntent.putExtra(IntentConstants.WALLET, oldIntent.getBooleanExtra(IntentConstants.WALLET, true))
                        }
                    }
                }
            }

        startActivity(startMainScreenIntent)
        finish()
    }
}
