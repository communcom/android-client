package io.golos.cyber_android.ui.screens.login.signup.verification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.widgets.SmsCodeWidget
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
                viewModel.onCodeChanged(code)
            }
        }

        next.setOnClickListener { sendCode() }

        smsCode.showKeyboard()
    }

    private fun sendCode() {
        viewModel.getCodeIfValid()?.let {
            signUpViewModel.verifySmsCode(it)
        }
        findNavController().navigate(R.id.action_signUpVerificationFragment_to_signUpNameFragment)
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            next.isEnabled = it
        })
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
