package io.golos.cyber_android.ui.screens.login.signin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.main.MainActivity
import io.golos.cyber_android.views.utils.BaseTextWatcher
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : LoadingFragment() {

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        login.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onLoginInput(s.toString())
            }
        })

        key.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onKeyInput(s.toString())
            }
        })

        key.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signIn.performClick()
                return@setOnEditorActionListener true
            }
            false
        }

        signIn.setOnClickListener {
            viewModel.signIn()
        }

        back.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getValidationResultLiveData.observe(this, Observer { isValid ->
            signIn.isEnabled = isValid
        })

        viewModel.loadingLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isLoading ->
                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        })

        viewModel.errorLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isError ->
                if (isError)
                    onError()
            }
        })

        viewModel.authStateLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { state ->
                if (state.isUserLoggedIn) {
                    navigateToMainScreen()
                }
            }
        })
    }


    private fun navigateToMainScreen() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    /**
     * Called when there was an error in login process, displays [NotificationDialog] with error message
     */
    private fun onError() {
        if (requireFragmentManager().findFragmentByTag("notification") == null)
            NotificationDialog
                .newInstance(getString(R.string.login_error))
                .show(requireFragmentManager(), "notification")
        hideLoading()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getSignInViewModelFactory()
        ).get(SignInViewModel::class.java)
    }
}
