package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpVerificationBinding
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.di.SignUpVerificationFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.ClearCodeCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.NavigateToUserNameCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view_model.SignUpVerificationViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.analytics.AnalyticsFacade
import kotlinx.android.synthetic.main.fragment_sign_up_verification.*
import javax.inject.Inject

class SignUpVerificationFragment : FragmentBaseMVVM<FragmentSignUpVerificationBinding, SignUpVerificationViewModel>() {
    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignUpVerificationViewModel> = SignUpVerificationViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_verification

    override fun inject(key: String) = App.injections.get<SignUpVerificationFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpVerificationFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpVerificationBinding, viewModel: SignUpVerificationViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        smsCode.setOnCodeChangedListener { viewModel.onCodeUpdated(it) }
        smsCode.setOnDonePressedListener { next.performClick() }
        next.setOnClickListener { viewModel.onNextClick() }
        resend.setOnClickListener { viewModel.onResendClick() }

        showKeyboardOnCodeInput()
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToSelectSignUpMethodCommand -> moveToSelectMethod()
            is NavigateToUserNameCommand -> moveToUserName()
            is ClearCodeCommand -> clearCode()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun moveToSelectMethod() = findNavController().navigate(R.id.action_signUpVerificationFragment2_to_signUpSelectMethodFragment)

    private fun moveToUserName() = findNavController().navigate(R.id.action_signUpVerificationFragment2_to_signUpNameFragment2)

    private fun showKeyboardOnCodeInput() {
        smsCode.post { smsCode.showKeyboard() }
    }

    private fun clearCode() = smsCode.clearCode()
}