package io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.shared.base.FragmentBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToInAppAuthScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint.view_commands.NavigateToKeysCommand
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import kotlinx.android.synthetic.main.fragment_fingerprint.*
import javax.inject.Inject

class FingerprintFragment : FragmentBase() {
    private lateinit var viewModel: FingerprintViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.getBase<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()

        unlockFingerprintButton.setOnClickListener {
            viewModel.onUnlockViaFingerprintClick()
        }

        unlockPasscodeButton.setOnClickListener {
            analyticsFacade.faceIDTouchIDActivated(false)
            viewModel.onUnlockViaPinCodeClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == InAppAuthActivity.REQUEST_CODE){
            analyticsFacade.faceIDTouchIDActivated(true)
            navigateToMainScreen()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FingerprintViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.command.observe(viewLifecycleOwner, Observer { command ->
            when (command) {
                is ShowMessageResCommand -> uiHelper.showMessage(command.textResId, command.isError)

                is NavigateToMainScreenCommand -> navigateToMainScreen()

                is NavigateToInAppAuthScreenCommand -> {
                    InAppAuthActivity.start(this, false)
                }
            }
        })
    }

    private fun navigateToMainScreen() {
        if (!requireActivity().isFinishing) {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }
}
