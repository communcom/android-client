package io.golos.cyber_android.ui.screens.app_start.sign_up.email.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.app_start.sign_up.email.model.SignUpEmailModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToEmailVerificationCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.ShowCaptchaCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpEmailViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpEmailModel,
    private val singUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper,
    analyticsFacade: AnalyticsFacade
) : ViewModelBase<SignUpEmailModel>(dispatchersProvider, model) {

    val email = MutableLiveData<String>("")

    private val _nextButtonEnabled = MutableLiveData<Boolean>(false)
    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled

    val isBackButtonVisible = false

    init {
//        analyticsFacade.openScreen112()

        launch {
            singUpCore.commands.collect { processSignUnCommand(it) }
        }

        email.observeForever {
            model.isEmailValid(it).let { isValid ->
                _nextButtonEnabled.value = isValid

//                    if(isValid) {
//                        analyticsFacade.phoneNumberEntered()
//                    }
            }
        }
    }

    fun onNextButtonClick() {
        _command.value = ShowCaptchaCommand()
    }

    fun onCaptchaReceived(captcha: String) {
        val emailToProcess = email.value!!

        _command.value = HideKeyboardCommand()
        singUpCore.process(EmailEntered(emailToProcess, captcha))
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is NavigateToEmailVerification -> _command.value = NavigateToEmailVerificationCommand()
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()
            else -> {}
        }
}