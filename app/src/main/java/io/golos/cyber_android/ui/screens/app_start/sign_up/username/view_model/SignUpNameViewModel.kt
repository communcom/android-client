package io.golos.cyber_android.ui.screens.app_start.sign_up.username.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.dto.NavigateToGetPasswordCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.dto.ValidationResult
import io.golos.cyber_android.ui.screens.app_start.sign_up.username.model.SignUpNameModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpNameViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpNameModel,
    private val signUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper,
    private val analyticsFacade: AnalyticsFacade,
    private val userNameValidationVisualizer: UserNameValidationVisualizer
) : ViewModelBase<SignUpNameModel>(dispatchersProvider, model) {

    val maxUserNameLen get() = model.maxUserNameLen

    private val _validationResult = MutableLiveData<ValidationResult>(ValidationResult(true, null))
    val validationResult: LiveData<ValidationResult> = _validationResult

    val userName = MutableLiveData<String>(model.userName)

    private val _nextButtonEnabled = MutableLiveData<Boolean>(false)
    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled

    val isBackButtonVisible = false

    init {
        analyticsFacade.openScreen114()

        launch {
            signUpCore.commands.collect { processSignUnCommand(it) }
        }

        userName.observeForever {
            _validationResult.value = model.updateUserName(it).let { moderValidationResult ->
                _nextButtonEnabled.value = moderValidationResult == UserNameValidationResult.SUCCESS

                when(moderValidationResult) {
                    UserNameValidationResult.SUCCESS -> ValidationResult(true, null)
                    else -> ValidationResult(false, userNameValidationVisualizer.toSting(moderValidationResult))
                }
            }
        }
    }

    fun onNextClick() {
        signUpCore.process(UserNameEntered(model.userName))
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()
            is NavigateToGetPassword -> _command.value = NavigateToGetPasswordCommand()
            else -> {}
        }
}