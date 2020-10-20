package io.golos.cyber_android.ui.screens.app_start.sign_up.email.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.safetynet.SafetyNet
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpEmailBinding
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.di.SignUpEmailFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.view_model.SignUpEmailViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToEmailVerificationCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.ShowCaptchaCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_email.*
import timber.log.Timber

class SignUpEmailFragment : FragmentBaseMVVM<FragmentSignUpEmailBinding, SignUpEmailViewModel>() {

    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignUpEmailViewModel> = SignUpEmailViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_email

    override fun inject(key: String) = App.injections.get<SignUpEmailFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpEmailFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpEmailBinding, viewModel: SignUpEmailViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUp.setOnClickListener { viewModel.onNextButtonClick() }

        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)

        email.requestFocus()
        uiHelper.setSoftKeyboardVisibility(email, true)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is HideKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(email, false)
            is NavigateToEmailVerificationCommand -> moveToEmailVerification()
            is NavigateToSelectSignUpMethodCommand -> moveToSelectMethod()
            is ShowCaptchaCommand -> startCaptcha()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun moveToEmailVerification() = findNavController().navigate(R.id.action_signUpEmailFragment_to_signUpEmailVerificationFragment)

    private fun moveToSelectMethod() = findNavController().navigate(R.id.action_signUpEmailFragment_to_signUpSelectMethodFragment)

    private fun startCaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(BuildConfig.GOOGLE_RECAPTCHA_KEY)
            .addOnSuccessListener { response ->
                val userResponseToken = response.tokenResult
                if (userResponseToken?.isNotEmpty() == true) {
                    viewModel.onCaptchaReceived(userResponseToken)
                } else {
                    uiHelper.showMessage(R.string.common_captcha_error)
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e)
                uiHelper.showMessage(R.string.common_captcha_error)
            }
    }
}