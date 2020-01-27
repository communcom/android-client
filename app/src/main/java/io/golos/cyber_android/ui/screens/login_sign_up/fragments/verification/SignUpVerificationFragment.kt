package io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenFragmentBase
import io.golos.data.errors.AppError
import io.golos.domain.use_cases.model.NextRegistrationStepRequestModel
import io.golos.domain.use_cases.model.ResendSmsVerificationCodeModel
import io.golos.domain.use_cases.model.SendVerificationCodeRequestModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_verification.*

class SignUpVerificationFragment : SignUpScreenFragmentBase<SignUpVerificationViewModel>(
    SignUpVerificationViewModel::class.java) {

    override val continueButton: View
        get() = next

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_verification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        close.setOnClickListener { findNavController().navigateUp() }

        smsCode.setOnCodeChangedListener { viewModel.onFieldChanged(it) }
        smsCode.setOnDonePressedListener { next.performClick() }

//        resend.setOnClickListener {
//            signUpViewModel.resendCode()
//            viewModel.restartResendTimer()
//        }
        next.setOnClickListener { sendCode() }

        showKeyboardOnCodeInput()
    }

    override fun inject() = App.injections.getBase<LoginActivityComponent>().inject(this)

    private fun sendCode() {
        viewModel.getFieldIfValid()?.let {
            signUpViewModel.verifyCode(it)
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()

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
                is QueryResult.Error -> onResendError()
                is QueryResult.Success -> onResendSuccess()
            }
        })

        viewModel.getSecondsUntilResendLiveData.observe(this, Observer { secondsRemain ->

            if (secondsRemain > 0) {
                resend.text = String.format(
                    getString(R.string.resend_verification_code_w_time_format),
                    secondsRemain
                )
                resend.isEnabled = false
            } else {
                resend.setText(R.string.resend_verification_code)
                resend.isEnabled = true
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
        val errorMsg = when(errorResult.error) {
            is AppError.ForbiddenError -> R.string.wrong_verification_code
            else -> R.string.unknown_error
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
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

    private fun onResendError() {
        hideLoading()
        Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show()
    }
}
