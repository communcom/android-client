package io.golos.cyber_android.ui.screens.login.signup.name

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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
import io.golos.cyber_android.views.utils.BaseTextWatcher
import kotlinx.android.synthetic.main.fragment_sign_up_name.*

class SignUpNameFragment : LoadingFragment() {

    private lateinit var viewModel: SignUpNameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        username.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onUsernameChanged(s.toString())
            }
        })

        username.filters = arrayOf(InputFilter.LengthFilter(SignUpNameViewModel.USERNAME_LENGTH))

        back.setOnClickListener { findNavController().navigateUp() }
        next.setOnClickListener { findNavController().navigate(R.id.action_signUpNameFragment_to_signUpKeyFragment) }
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            next.isEnabled = it
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this
        ).get(SignUpNameViewModel::class.java)

        val signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
