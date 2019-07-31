package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.widgets.pin.Digit
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeValidationResult
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.ResetPinCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SetPinCodeDigitCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToFingerprintCommand
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PinCodeAuthViewModel
@Inject
constructor(
    @StringRes private val headerText: Int,
    private val appResourcesProvider: AppResourcesProvider,
    dispatchersProvider: DispatchersProvider,
    model: PinCodeAuthModel
) : ViewModelBase<PinCodeAuthModel>(dispatchersProvider, model) {

    private val normalColor = appResourcesProvider.getColor(R.color.black)
    private val errorColor by lazy { appResourcesProvider.getColor(R.color.red) }

    val title: MutableLiveData<String> = MutableLiveData(appResourcesProvider.getString(headerText))

    val switchButtonVisibility: MutableLiveData<Int> = MutableLiveData(if(model.isFingerprintAuthPossible) View.VISIBLE else View.INVISIBLE)

    val messageText: MutableLiveData<String> = MutableLiveData(appResourcesProvider.getString(R.string.authEnterPinCode))
    val messageColor: MutableLiveData<Int> = MutableLiveData(normalColor)

    init {
        launch {
            model.initModel()
        }
    }

    fun onInactive() {
        model.reset()
        command.value = ResetPinCommand()
    }

    fun onSwitchToFingerprintClick() {
        command.value = SwitchToFingerprintCommand()
    }

    fun onDigitPressed(digit: Digit) {
        command.value = SetPinCodeDigitCommand(digit)

        when(model.processDigit(digit)) {
            PinCodeValidationResult.VALID -> command.value = AuthSuccessCommand()

            PinCodeValidationResult.INVALID -> {
                messageText.value = appResourcesProvider.getString(R.string.authPinCodeAuthFail)
                messageColor.value = errorColor

                model.reset()
                command.value = ResetPinCommand()
            }

            else -> {}
        }
    }
}