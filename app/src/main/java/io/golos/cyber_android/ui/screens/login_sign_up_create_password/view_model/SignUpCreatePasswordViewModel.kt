package io.golos.cyber_android.ui.screens.login_sign_up_create_password.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.screens.wallet_send_points.dto.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignUpCreatePasswordViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpCreatePasswordModel
) : ViewModelBase<SignUpCreatePasswordModel>(dispatchersProvider, model) {

    val minLen = model.passwordValidator.minLen
    val maxLen = model.passwordValidator.maxLen

    private val _validationResult = MutableLiveData<List<PasswordValidationCase>>(listOf())
    val validationResult: LiveData<List<PasswordValidationCase>> = _validationResult

    val password = MutableLiveData<String>("")

    private val _isPasswordVisible = MutableLiveData<Boolean>(true)
    val isPasswordVisible: LiveData<Boolean> = _isPasswordVisible

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    val screenTitle: String = model.screenTitle
    val passwordHint: String = model.passwordHint

    init {
        password.observeForever {
            val validationResult = model.passwordValidator.validate(it)
            _validationResult.value = validationResult

            _isNextButtonEnabled.value = model.passwordValidator.getFirstInvalidCase(validationResult) == null
        }
    }

    fun onVisibilityButtonClick() {
        _isPasswordVisible.value = !isPasswordVisible.value!!
    }

    fun onValidationCaseClick(case: PasswordValidationCase) {
        _command.value = ShowMessageTextCommand(model.passwordValidator.getExplanation(case), false)
    }

    fun onNextButtonClick() {
        _validationResult.value
            ?.let {
                val firstInvalidCase = model.passwordValidator.getFirstInvalidCase(it)
                if(firstInvalidCase != null) {
                    _command.value = ShowMessageTextCommand(model.passwordValidator.getExplanation(firstInvalidCase), false)
                } else {
                    model.savePassword(password.value!!)
                    _command.value = HideSoftKeyboardCommand()
                    // move next
                }
            }
    }
}