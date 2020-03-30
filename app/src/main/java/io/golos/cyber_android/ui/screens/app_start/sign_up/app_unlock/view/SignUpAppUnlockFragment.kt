package io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.view

import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view.SignInAppUnlockFragment
import io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.di.SignUpAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.view_model.SignUpAppUnlockViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToInAppAuthScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

open class SignUpAppUnlockFragment : SignInAppUnlockFragment() {
    override val isBackHandlerEnabled: Boolean = true

    override fun inject(key: String) = App.injections.get<SignUpAppUnlockFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpAppUnlockFragmentComponent>(key)

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpAppUnlockViewModel::class.java)
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToMainScreenCommand -> navigateToMainScreen()
            is NavigateToSelectSignUpMethodCommand -> findNavController().navigate(R.id.action_signUpAppUnlockFragment2_to_signUpSelectMethodFragment)
            is NavigateToInAppAuthScreenCommand -> InAppAuthActivity.start(this, false)
        }
    }
}
