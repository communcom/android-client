package io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.view

import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.view.SignUpCreatePasswordFragment
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToNextScreen
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_create_password.*

class SignUpConfirmPasswordFragment() : SignUpCreatePasswordFragment() {

    override fun inject(key: String) = App.injections.get<SignUpConfirmPasswordFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpConfirmPasswordFragmentComponent>(key)

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is HideSoftKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(password, false)

            is NavigateToNextScreen ->
                findNavController().navigate(R.id.action_signUpConfirmPasswordFragment_to_pinCodeFragment)

            is NavigateBackwardCommand -> findNavController().navigateUp()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

}