package io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInQrCodeBinding
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.detector.QrCodeDetector
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view_model.SignInQrCodeViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_in_qr_code.*
import javax.inject.Inject

class SignInQrCodeFragment : FragmentBaseMVVM<FragmentSignInQrCodeBinding, SignInQrCodeViewModel>() {
    @Inject
    internal lateinit var detector: QrCodeDetector

    override fun provideViewModelType(): Class<SignInQrCodeViewModel> = SignInQrCodeViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in_qr_code

    override fun inject(key: String) = App.injections.get<SignInQrCodeFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignInQrCodeFragmentComponent>(key)

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