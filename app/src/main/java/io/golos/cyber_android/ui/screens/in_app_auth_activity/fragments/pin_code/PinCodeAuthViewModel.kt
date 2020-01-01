package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.widgets.pin.Digit
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model.PinCodeValidationResult
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.ResetPinCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SetPinCodeDigitCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToFingerprintCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class PinCodeAuthViewModel
@Inject
constructor(
    private val context: Context,
    @StringRes private val headerText: Int,
    dispatchersProvider: DispatchersProvider,
    model: PinCodeAuthModel
) : ViewModelBase<PinCodeAuthModel>(dispatchersProvider, model) {

    private val normalColor = context.resources.getColorRes(R.color.black)
    private val errorColor by lazy { context.resources.getColorRes(R.color.red) }

    val title: MutableLiveData<String> = MutableLiveData(context.resources.getString(headerText))

    val switchButtonVisibility: MutableLiveData<Int> = MutableLiveData(if(model.isFingerprintAuthPossible) View.VISIBLE else View.INVISIBLE)

    val messageText: MutableLiveData<String> = MutableLiveData(context.resources.getString(R.string.authEnterPinCode))
    val messageColor: MutableLiveData<Int> = MutableLiveData(normalColor)

    init {
        launch {
            model.initModel()
        }
    }

    fun onInactive() {
        model.reset()
        _command.value = ResetPinCommand()
    }

    fun onSwitchToFingerprintClick() {
        _command.value = SwitchToFingerprintCommand()
    }

    fun onDigitPressed(digit: Digit) {
        _command.value = SetPinCodeDigitCommand(digit)

        when(model.processDigit(digit)) {
            PinCodeValidationResult.VALID -> _command.value = AuthSuccessCommand()

            PinCodeValidationResult.INVALID -> {
                messageText.value = context.resources.getString(R.string.authPinCodeAuthFail)
                messageColor.value = errorColor

                model.reset()
                _command.value = ResetPinCommand()
            }

            else -> {}
        }
    }
}