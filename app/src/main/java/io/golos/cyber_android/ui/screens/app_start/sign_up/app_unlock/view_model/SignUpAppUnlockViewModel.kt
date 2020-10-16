package io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.view_model

import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model.AppUnlockModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view_model.SignInAppUnlockViewModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.domain.dto.AppUnlockWay
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

open class SignUpAppUnlockViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: AppUnlockModel,
    analyticsFacade: AnalyticsFacade,
    private val singUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper
) : SignInAppUnlockViewModel(
    dispatchersProvider,
    model,
    analyticsFacade
) {

    init {
        launch {
            singUpCore.commands.collect { processSignUnCommand(it) }
        }
    }

    override fun saveUnlockWay(appUnlockWay: AppUnlockWay) {
        launch {
            model.saveAppUnlockWay(appUnlockWay)
        }
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()
            is SingUpCompleted -> _command.value = NavigateToMainScreenCommand()
            else -> {}
        }
}