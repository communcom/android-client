package io.golos.cyber_android.ui.screens.app_start.welcome.activity.view_model

import android.content.Context
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.HideSplashAnimationCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.NavigateToWelcomeScreenCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.dto.ShowSplashAnimationCommand
import io.golos.cyber_android.ui.screens.app_start.welcome.activity.model.WelcomeModel
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class WelcomeViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WelcomeModel
) : ViewModelBase<WelcomeModel>(dispatchersProvider, model) {

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

            try {
                model.login()
                hideSplashAnimation()
                _command.value = NavigateToMainScreenCommand()
            } catch (ex: Exception) {
                hideSplashAnimation()
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))
                _command.value = NavigateToWelcomeScreenCommand()
            }
        }
    }

    private suspend fun hideSplashAnimation() {
        _command.value = HideSplashAnimationCommand()
        delay(splashAnimationDuration)          // To complete the animation
    }
}