package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment.FingerprintAuthFragmentComponent
import io.golos.cyber_android.databinding.FragmentFingerprintAuthBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.Navigator
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToFingerprintCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToPinCodeCommand
import javax.inject.Inject

/**
 * Fragment for authentication via fingerprint
 */
class FingerprintAuthFragment : FragmentBaseMVVM<FragmentFingerprintAuthBinding, FingerprintAuthModel, FingerprintAuthViewModel>() {

    @Inject
    internal lateinit var navigator: Navigator

    override fun provideViewModelType(): Class<FingerprintAuthViewModel> = FingerprintAuthViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_fingerprint_auth

    override fun inject() = App.injections
        .get<FingerprintAuthFragmentComponent>(arguments!!.getInt(InAppAuthActivity.FINGERPRINT_HEADER_ID))
        .inject(this)

    override fun linkViewModel(binding: FragmentFingerprintAuthBinding, viewModel: FingerprintAuthViewModel) {
        binding.viewModel = viewModel
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
