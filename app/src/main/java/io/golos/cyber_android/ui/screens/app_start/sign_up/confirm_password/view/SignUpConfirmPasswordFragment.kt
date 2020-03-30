package io.golos.cyber_android.ui.screens.app_start.sign_up.confirm_password.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_up.confirm_password.di.SignUpConfirmPasswordFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.SignUpCreatePasswordFragment
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToNextScreen
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_create_password.*

class SignUpConfirmPasswordFragment() : SignUpCreatePasswordFragment() {

    override fun inject(key: String) = App.injections.get<SignUpConfirmPasswordFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpConfirmPasswordFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header.setOnBackButtonClickListener { viewModel.onBackButtonClick() }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is HideSoftKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(password, false)

            is NavigateToNextScreen -> findNavController().navigate(R.id.action_signUpConfirmPasswordFragment2_to_signUpPinCodeFragment2)

            is NavigateBackwardCommand -> findNavController().navigate(R.id.action_signUpConfirmPasswordFragment2_to_signUpCreatePasswordFragment2)

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    override fun onBackPressed() {
        viewModel.onBackButtonClick()
    }
}