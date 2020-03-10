package io.golos.cyber_android.ui.screens.login_activity.view_model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_activity.dto.*
import io.golos.cyber_android.ui.screens.login_activity.model.LoginModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class LoginViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: LoginModel
) : ViewModelBase<LoginModel>(dispatchersProvider, model) {

    init {
        processLogin()
    }

    val splashAnimationDuration = 500L // ms

    fun processLogin() {
        launch {
            _command.value = ShowSplashAnimationCommand()

            if(!model.hasNetworkConnection) {
                delay(2000)
                hideSplashAnimation()
                _command.value = ShowNoConnectionDialogCommand()
                return@launch
            } else {
                _command.value = HideNoConnectionDialogCommand()
            }

            if(model.isOutdated()) {
                _command.value = ShowUpdateAppDialogCommand()
                return@launch
            }

            if(!model.hasAuthState()) {
                hideSplashAnimation()
                _command.value = NavigateToWelcomeScreenCommand()
                return@launch
            }

            if(!model.isSetupCompleted) {
                hideSplashAnimation()
                _command.value = NavigateToContinueSetupScreenCommand()
                return@launch
            }

            if(model.login()) {
                hideSplashAnimation()
                _command.value = NavigateToMainScreenCommand()
            } else {
                hideSplashAnimation()
                _command.value = ShowMessageResCommand(R.string.common_general_error)
                _command.value = NavigateToWelcomeScreenCommand()
            }
        }
    }

    private suspend fun hideSplashAnimation() {
        _command.value = HideSplashAnimationCommand()
        delay(splashAnimationDuration)          // To complete the animation
    }
}