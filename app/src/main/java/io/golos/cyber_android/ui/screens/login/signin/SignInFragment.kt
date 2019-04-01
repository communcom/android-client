package io.golos.cyber_android.ui.screens.login.signin

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.dialogs.LoadingDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.views.utils.BaseTextWatcher
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private lateinit var viewModel: SignInViewModel

    private val loadingDialog = LoadingDialog()

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
                super.afterTextChanged(s)
                viewModel.onLoginInput(s.toString())
            }
        })

        key.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onKeyInput(s.toString())
            }
        })

        signIn.setOnClickListener {
            viewModel.signIn()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.validationResultLiveData.observe(this, Observer { isValid ->
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
    }

    private fun showLoading() {
        if (loadingDialog.dialog?.isShowing != true)
            loadingDialog.show(requireFragmentManager(), "loading")
    }

    private fun hideLoading() {
        if (loadingDialog.dialog?.isShowing == true)
            loadingDialog.dismiss()
    }

    /**
     * Called when there was an error in login process, displays [NotificationDialog] with error message
     */
    private fun onError() {
        NotificationDialog
            .newInstance(getString(R.string.login_error))
            .show(requireFragmentManager(), "notification")
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
