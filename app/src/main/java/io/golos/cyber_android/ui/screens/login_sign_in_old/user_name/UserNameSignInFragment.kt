package io.golos.cyber_android.ui.screens.login_sign_in_old.user_name

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.login_sign_in_old.SignInArgs
import io.golos.cyber_android.ui.screens.login_sign_in_old.SignInChildFragment
import io.golos.cyber_android.ui.screens.login_sign_in_old.SignInParentFragment
import io.golos.cyber_android.ui.screens.login_sign_in_old.SignInTab
import io.golos.cyber_android.ui.utils.TextWatcherBase
import kotlinx.android.synthetic.main.fragment_sign_in_user_name_old.*
import javax.inject.Inject

class UserNameSignInFragment : FragmentBase(), SignInChildFragment {
    companion object {
        fun newInstance(tab: SignInTab) =
            UserNameSignInFragment()
                .apply {
                    arguments = Bundle().apply { putInt(SignInArgs.TAB_CODE, tab.index) }
                }
    }

    private lateinit var viewModel: UserNameSignInViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override val tabCode: SignInTab by lazy {
        arguments!!.getInt(SignInArgs.TAB_CODE).let { SignInTab.fromIndex(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_in_user_name_old, container, false)

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
                is ShowMessageResCommand -> uiHelper.showMessage(command.textResId)
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserNameSignInViewModel::class.java)
    }
}
