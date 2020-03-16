package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view_model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.dto.QrCodeDetectorErrorCode
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.model.SignInQrCodeModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignInQrCodeViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignInQrCodeModel,
    private val dataPass: LoginActivityFragmentsDataPass
) : ViewModelBase<SignInQrCodeModel>(dispatchersProvider, model) {

    private var isCodeReceived = false

    fun onCodeReceived(code: QrCodeContent) {
        if(isCodeReceived) {
            return              // To prevent events spam
        }

        isCodeReceived = true

        dataPass.putQrCode(code)

        _command.value = NavigateBackwardCommand()
    }

    fun onError(errorCode: QrCodeDetectorErrorCode) {
        when(errorCode) {
            QrCodeDetectorErrorCode.INVALID_CODE -> {
                _command.value = ShowMessageResCommand(R.string.sign_in_scan_qr_invalid_format)
            }

            QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL -> {
                _command.value = ShowMessageResCommand(R.string.sign_in_scan_qr_detector_error)
                _command.value = NavigateBackwardCommand()
            }
        }
    }

    fun onBackButtonClick() {
        _command.value = NavigateBackwardCommand()
    }
}