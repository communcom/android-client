package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFingerprintAuthBinding
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di.FingerprintAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.Navigator
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToPinCodeCommand
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_fingerprint_auth.*
import javax.inject.Inject

/**
 * Fragment for authentication via fingerprint
 */
class FingerprintAuthFragment : FragmentBaseMVVM<FragmentFingerprintAuthBinding, FingerprintAuthViewModel>() {

    @Inject
    internal lateinit var navigator: Navigator

    override fun provideViewModelType(): Class<FingerprintAuthViewModel> = FingerprintAuthViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_fingerprint_auth

    override fun releaseInjection(key: String) = App.injections.release<FingerprintAuthFragmentComponent>(key)

    override fun inject(key: String) = App.injections
        .get<FingerprintAuthFragmentComponent>(key, arguments!!.getInt(InAppAuthActivity.FINGERPRINT_HEADER_ID))
        .inject(this)

    override fun linkViewModel(binding: FragmentFingerprintAuthBinding, viewModel: FingerprintAuthViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchToPinButton.visibility = if(arguments!!.getBoolean(InAppAuthActivity.PIN_CODE_UNLOCK_ENABLED)) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActive()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onInactive()
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is AuthSuccessCommand -> navigator.processAuthSuccess(requireActivity())
            is SwitchToPinCodeCommand -> navigator.moveToPinCode(this)
        }
    }
}
