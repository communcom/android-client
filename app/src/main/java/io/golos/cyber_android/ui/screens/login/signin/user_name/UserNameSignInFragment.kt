package io.golos.cyber_android.ui.screens.login.signin.user_name

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.login.signin.SignInArgs
import io.golos.cyber_android.ui.screens.login.signin.SignInChildFragment
import io.golos.cyber_android.ui.screens.login.signin.SignInParentFragment
import io.golos.cyber_android.ui.screens.login.signin.SignInTab
import io.golos.cyber_android.ui.screens.main.MainActivity
import io.golos.cyber_android.views.utils.TextWatcherBase
import kotlinx.android.synthetic.main.fragment_user_name_sign_in.*

class UserNameSignInFragment : FragmentBase(), SignInChildFragment {
    companion object {
        fun newInstance(tab: SignInTab) =
            UserNameSignInFragment()
                .apply {
                    arguments = Bundle().apply { putInt(SignInArgs.TAB_CODE, tab.index) }
                }
    }

    private lateinit var viewModel: UserNameSignInViewModel

    override val tabCode: SignInTab by lazy {
        arguments!!.getInt(SignInArgs.TAB_CODE).let { SignInTab.fromIndex(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_user_name_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        login.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onLoginInput(s.toString())
            }
        })

        key.addTextChangedListener(object : TextWatcherBase() {
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

        restoreFromCloudButton.setOnClickListener {
            viewModel.onRestoreFromCloud()
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
                    navigateForward()
                }
            }
        })

        viewModel.restoreFromCloudButtonEnabled.observe(this, Observer {
            restoreFromCloudButton.isEnabled = it
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
                is SetKeyValueViewCommand -> key.setText(command.newKeyValue)
            }
        })
    }

    private fun navigateForward() {
        (requireParentFragment() as SignInParentFragment).navigateForward()
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
                .getDefaultViewModelFactory()
        ).get(UserNameSignInViewModel::class.java)
    }
}
