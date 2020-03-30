package io.golos.cyber_android.ui.screens.app_start.sign_up.pin.view_model

import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model.PinCodeViewModelBase
import io.golos.cyber_android.ui.screens.app_start.sign_up.pin.model.SignUpPinCodeModel
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpPinCodeViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    private val model: SignUpPinCodeModel,
    private val signUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper
) : PinCodeViewModelBase(
    dispatchersProvider,
    model
) {
    init {
        launch {
            signUpCore.commands.collect { processSignUnCommand(it) }
        }
    }

    override suspend fun saveCode() {
        model.saveCode()
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()
            is NavigateToSelectUnlock -> _command.value = NavigateToFingerprintCommand()
            is SingUpCompleted -> _command.value = NavigateToMainScreenCommand()
            else -> {}
        }
}