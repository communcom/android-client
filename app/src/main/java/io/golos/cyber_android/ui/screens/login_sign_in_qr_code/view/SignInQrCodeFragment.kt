package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInQrCodeBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view.detector.QrCodeDetector
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view_model.SignInQrCodeViewModel
import kotlinx.android.synthetic.main.fragment_sign_in_qr_code.*
import javax.inject.Inject

class SignInQrCodeFragment : FragmentBaseMVVM<FragmentSignInQrCodeBinding, SignInQrCodeViewModel>() {
    @Inject
    internal lateinit var detector: QrCodeDetector

    override fun provideViewModelType(): Class<SignInQrCodeViewModel> = SignInQrCodeViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in_qr_code

    override fun inject() = App.injections.get<SignInQrCodeFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<SignInQrCodeFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentSignInQrCodeBinding, viewModel: SignInQrCodeViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detector.setOnCodeReceivedListener { viewModel.onCodeReceived(it) }
        detector.setOnDetectionErrorListener { viewModel.onError(it) }
    }

    override fun onResume() {
        super.onResume()
        detector.startDetection(cameraSurface)
    }

    override fun onPause() {
        super.onPause()
        detector.stopDetection()
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> findNavController().navigateUp()
        }
    }
}