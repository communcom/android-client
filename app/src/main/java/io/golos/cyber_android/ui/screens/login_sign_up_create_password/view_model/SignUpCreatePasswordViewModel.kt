package io.golos.cyber_android.ui.screens.login_sign_up_create_password.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordProcessingResult
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
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

    private val _isPasswordVisible = MutableLiveData<Boolean>(false)
    val isPasswordVisible: LiveData<Boolean> = _isPasswordVisible

    private val _isInvalidCharactersLabelVisible = MutableLiveData<Int>(View.INVISIBLE)
    val isInvalidCharactersLabelVisible: LiveData<Int> = _isInvalidCharactersLabelVisible

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    val screenTitle: String = model.screenTitle
    val passwordHint: String = model.passwordHint

    init {
        password.observeForever {
            val validationResult = model.passwordValidator.validate(it)
            _validationResult.value = validationResult

            val hasInvalidCharacters = model.passwordValidator.hasInvalidCharacters(it)
            val isPassValid = model.passwordValidator.getFirstInvalidCase(validationResult) == null && !hasInvalidCharacters

            _isNextButtonEnabled.value = isPassValid
            _isInvalidCharactersLabelVisible.value = if(hasInvalidCharacters) View.VISIBLE else View.INVISIBLE
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
                    launch {
                        _command.value = SetLoadingVisibilityCommand(true)
                        val processingResult = model.processPassword(password.value!!)
                        _command.value = SetLoadingVisibilityCommand(false)
                        when(processingResult) {
                            PasswordProcessingResult.SUCCESS -> {
                                _command.value = HideSoftKeyboardCommand()
                                _command.value = NavigateToNextScreen()
                            }
                            PasswordProcessingResult.PASSWORD_IS_NOT_CONFIRMED -> {
                                _command.value = ShowMessageResCommand(R.string.password_not_match)
                            }
                        }
                    }
                }
            }
    }

    fun onBackButtonClick() {
        _command.value = NavigateBackwardCommand()
    }
}