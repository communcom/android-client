package io.golos.cyber_android.ui.screens.app_start.sign_up.pin.view

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view.SignInPinCodeFragment
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.di.SignUpPinCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.view_model.SignUpPinCodeViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

class SignUpPinCodeFragment : SignInPinCodeFragment() {
    override val isBackHandlerEnabled: Boolean = true

    override fun inject(key: String) = App.injections.get<SignUpPinCodeFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpPinCodeFragmentComponent>(key)

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpPinCodeViewModel::class.java)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToFingerprintCommand -> findNavController().navigate(R.id.action_signUpPinCodeFragment2_to_signUpAppUnlockFragment2)
            is NavigateToMainScreenCommand -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
            is NavigateToSelectSignUpMethodCommand -> findNavController().navigate(R.id.action_signUpPinCodeFragment2_to_signUpSelectMethodFragment)
        }
    }
}
