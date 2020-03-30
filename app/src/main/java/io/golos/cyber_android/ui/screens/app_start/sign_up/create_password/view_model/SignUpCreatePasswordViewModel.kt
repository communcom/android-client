package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

open class SignUpCreatePasswordViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpCreatePasswordModel,
    protected val signUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper
) : ViewModelBase<SignUpCreatePasswordModel>(dispatchersProvider, model) {

    val minLen = model.passwordValidator.minLen
    val maxLen = model.passwordValidator.maxLen

    private val _validationResult = MutableLiveData<List<PasswordValidationCase>>(listOf())
    val validationResult: LiveData<List<PasswordValidationCase>> = _validationResult

    val password = MutableLiveData<String>(getInitPassword())

    private val _isPasswordVisible = MutableLiveData<Boolean>(false)
    val isPasswordVisible: LiveData<Boolean> = _isPasswordVisible

    private val _isInvalidCharactersLabelVisible = MutableLiveData<Int>(View.INVISIBLE)
    val isInvalidCharactersLabelVisible: LiveData<Int> = _isInvalidCharactersLabelVisible

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    val screenTitle: String = model.screenTitle
    val passwordHint: String = model.passwordHint

    open val isBackButtonVisible = false

    init {
        password.observeForever {
            val validationResult = model.passwordValidator.validate(it)
            _validationResult.value = validationResult

            val hasInvalidCharacters = model.passwordValidator.hasInvalidCharacters(it)
            val isPassValid = model.passwordValidator.getFirstInvalidCase(validationResult) == null && !hasInvalidCharacters

            _isNextButtonEnabled.value = isPassValid
            _isInvalidCharactersLabelVisible.value = if(hasInvalidCharacters) View.VISIBLE else View.INVISIBLE
        }

        launch {
            signUpCore.commands.collect { processSignUnCommand(it) }
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
                    model.processPassword(password.value!!)
                }
            }
    }

    open fun onBackButtonClick() {}

    protected open fun getInitPassword() = signUpCore.getSnapshot().password ?: ""

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToConfirmPassword -> {
                _command.value = HideSoftKeyboardCommand()
                _command.value = NavigateToNextScreen()
            }
            is NavigateToPinCode -> {
                _command.value = HideSoftKeyboardCommand()
                _command.value = NavigateToNextScreen()
            }
            else -> {}
        }
}