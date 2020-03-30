package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.SignInPinCodeModel
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignInPinCodeViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val model: SignInPinCodeModel
) : PinCodeViewModelBase(
    dispatchersProvider,
    model
) {
    override suspend fun saveCode() {
        if(model.saveCode()) {
            _command.value = if(model.isFingerprintAuthenticationPossible) {
                NavigateToFingerprintCommand()
            } else {
                model.saveKeysExported()
                NavigateToMainScreenCommand()
            }
        } else {
            _command.value = ShowMessageResCommand(R.string.common_general_error)
        }
    }
}