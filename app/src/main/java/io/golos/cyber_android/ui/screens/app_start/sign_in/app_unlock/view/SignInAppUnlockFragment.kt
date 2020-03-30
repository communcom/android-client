package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.di.SignInAppUnlockFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view_model.SignInAppUnlockViewModel
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.ui.shared.base.FragmentBaseCoroutines
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToInAppAuthScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_fingerprint.*
import javax.inject.Inject

open class SignInAppUnlockFragment : FragmentBaseCoroutines() {
    override val isBackHandlerEnabled: Boolean = true

    protected lateinit var viewModel: SignInAppUnlockViewModel

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    override fun inject(key: String) = App.injections.get<SignInAppUnlockFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignInAppUnlockFragmentComponent>(key)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()

        unlockFingerprintButton.setOnClickListener { viewModel.onUnlockViaFingerprintClick()}
        unlockPasscodeButton.setOnClickListener { viewModel.onUnlockViaPinCodeClick() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == InAppAuthActivity.REQUEST_CODE){
            viewModel.onFingerprintConfirmed()
        }
    }

    protected open fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignInAppUnlockViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.command.observe(viewLifecycleOwner, Observer { processViewCommandGeneral(it)})
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToMainScreenCommand -> navigateToMainScreen()
            is NavigateToInAppAuthScreenCommand -> InAppAuthActivity.start(this, false)
        }
    }

    protected fun navigateToMainScreen() {
        if (!requireActivity().isFinishing) {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }
}
