package io.golos.cyber_android.ui.screens.login.signup.verification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.widgets.SmsCodeWidget
import io.golos.domain.interactors.model.NextRegistrationStepRequestModel
import io.golos.domain.interactors.model.ResendSmsVerificationCodeModel
import io.golos.domain.interactors.model.SendVerificationCodeRequestModel
import io.golos.domain.model.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_verification.*

class SignUpVerificationFragment : LoadingFragment() {

    private lateinit var viewModel: SignUpVerificationViewModel

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_verification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        close.setOnClickListener { findNavController().navigateUp() }

        smsCode.listener = object : SmsCodeWidget.Listener {
            override fun sendCode(code: String) {
                next.performClick()
            }

            override fun onCodeChanged(code: String) {
                viewModel.onFieldChanged(code)
            }
        }

        resend.setOnClickListener { signUpViewModel.resendCode() }
        next.setOnClickListener { sendCode() }

        showKeyboardOnCodeInput()
    }

    private fun sendCode() {
        viewModel.getFieldIfValid()?.let {
            signUpViewModel.verifyCode(it)
        }
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            next.isEnabled = it
        })

        signUpViewModel.getUpdatingStateForStep<SendVerificationCodeRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
                is QueryResult.Success -> onSuccess()
            }
        })

        signUpViewModel.getUpdatingStateForStep<ResendSmsVerificationCodeModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onResendError(it)
                is QueryResult.Success -> onResendSuccess()
            }
        })
    }

    private fun onSuccess() {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpVerificationFragment,
            R.id.action_signUpVerificationFragment_to_signUpNameFragment
        )
    }

    private fun onError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        Toast.makeText(requireContext(), errorResult.error.message, Toast.LENGTH_SHORT).show()
        smsCode.clearCode()
        showKeyboardOnCodeInput()
    }

    private fun showKeyboardOnCodeInput() {
        smsCode.post {
            smsCode.showKeyboard()
        }
    }

    private fun onResendSuccess() {
        hideLoading()
        Toast.makeText(requireContext(), "Resend success", Toast.LENGTH_SHORT).show()
        smsCode.clearCode()
        showKeyboardOnCodeInput()
    }

    private fun onResendError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        Toast.makeText(requireContext(), errorResult.error.message, Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this
        ).get(SignUpVerificationViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
