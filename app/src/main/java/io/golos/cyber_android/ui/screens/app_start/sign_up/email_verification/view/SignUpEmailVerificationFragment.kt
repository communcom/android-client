package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpEmailVerificationBinding
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.di.SignUpEmailVerificationFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.view_model.SignUpEmailVerificationViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.NavigateToUserNameCommand
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.analytics.AnalyticsFacade
import kotlinx.android.synthetic.main.fragment_sign_up_email_verification.*
import javax.inject.Inject

class SignUpEmailVerificationFragment : FragmentBaseMVVM<FragmentSignUpEmailVerificationBinding, SignUpEmailVerificationViewModel>() {
    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignUpEmailVerificationViewModel> = SignUpEmailVerificationViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_email_verification

    override fun inject(key: String) = App.injections.get<SignUpEmailVerificationFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpEmailVerificationFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpEmailVerificationBinding, viewModel: SignUpEmailVerificationViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        next.setOnClickListener { viewModel.onNextClick() }
        resend.setOnClickListener { viewModel.onResendClick() }

        code.requestFocus()
        uiHelper.setSoftKeyboardVisibility(code, true)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateToSelectSignUpMethodCommand -> moveToSelectMethod()
            is NavigateToUserNameCommand -> moveToUserName()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun moveToSelectMethod() = findNavController().navigate(R.id.action_signUpEmailVerificationFragment_to_signUpSelectMethodFragment)

    private fun moveToUserName() = findNavController().navigate(R.id.action_signUpEmailVerificationFragment_to_signUpNameFragment2)
}