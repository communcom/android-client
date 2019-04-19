package io.golos.cyber_android.ui.screens.login.signup.name

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.ViewUtils
import io.golos.domain.interactors.model.*
import io.golos.domain.model.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_name.*

class SignUpNameFragment : LoadingFragment() {

    private lateinit var viewModel: SignUpNameViewModel

    private lateinit var signUpViewModel: SignUpViewModel

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
                viewModel.onFieldChanged(s.toString())
            }
        })

        username.filters = arrayOf(InputFilter.LengthFilter(SignUpNameViewModel.USERNAME_LENGTH))

        back.setOnClickListener { findNavController().navigateUp() }
        next.setOnClickListener {
            viewModel.getFieldIfValid()?.let {
                signUpViewModel.sendName(it)
            }
        }

        username.post {
            ViewUtils.showKeyboard(username)
        }
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            next.isEnabled = it
        })

        signUpViewModel.getUpdatingStateForStep<SetUserNameRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.stateLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                if (it is UnWrittenToBlockChainUserModel)
                    signUpViewModel.writeToBlockchain()
            }
        })

        signUpViewModel.getUpdatingStateForStep<WriteUserToBlockChainRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.keysLiveData.asEvent().observe(this, Observer {
            onSuccess()
        })
    }

    private fun onSuccess() {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpNameFragment,
            R.id.action_signUpNameFragment_to_signUpKeyFragment
        )
    }

    private fun onError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        Toast.makeText(requireContext(), errorResult.error.message, Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this
        ).get(SignUpNameViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
