package io.golos.cyber_android.ui.screens.app_start.sign_in.username.view_model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Lazy
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.app_start.sign_in.shared.data_pass.SignInFragmentsDataPass
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.NavigateToQrCodeCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.NavigateToSignUpCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.SetPasswordFocusCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.dto.SetUserNameFocusCommand
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.SignInUserNameModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SignInUserNameViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignInUserNameModel,
    private val userNameValidationVisualizer: Lazy<UserNameValidationVisualizer>,
    private val passwordValidationVisualizer: Lazy<PasswordValidationVisualizer>,
    private val dataPass: SignInFragmentsDataPass
) : ViewModelBase<SignInUserNameModel>(dispatchersProvider, model) {

    private val _signInButtonEnabled = MutableLiveData<Boolean>(false)
    val signInButtonEnabled: LiveData<Boolean> get() = _signInButtonEnabled

    val userName = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _pastePasswordVisibility = MutableLiveData<Int>(View.GONE)
    val pastePasswordVisibility: LiveData<Int> get() = _pastePasswordVisibility

    private val _pastePasswordText = MutableLiveData<String>()
    val pastePasswordText: LiveData<String> get() = _pastePasswordText


    val maxUserNameLen: Int
        get() = model.maxUserNameLen

    val maxPasswordLen: Int
        get() = model.maxPasswordLen

    val isBackButtonVisible = false

    init {
        userName.observeForever {
            _signInButtonEnabled.value = it.length >= model.minUserNameLen && password.value!!.length >= model.minPasswordLen
        }

        password.observeForever {
            _signInButtonEnabled.value = it.length >= model.minPasswordLen && userName.value!!.length >= model.minUserNameLen
        }
    }

    fun processResumedActions() {
        tryToGetPassFromClipboard()
        tryToGetQrCode()
    }

    fun onBackButtonClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onPastePasswordClick() {
        val clipPassword = model.getPasswordFromClipboard()
        if(clipPassword != null) {
            password.value = clipPassword
        }
        //_pastePasswordVisibility.value = View.GONE
    }

    fun onSignInClick() {
        // To prevent Done action on a soft keyboard
        if(!_signInButtonEnabled.value!!) {
            return
        }

        // User's name validation
        val userNameValidation = model.validateUserName(userName.value!!)
        if(userNameValidation != UserNameValidationResult.SUCCESS) {
            _command.value = SetUserNameFocusCommand()
            _command.value = ShowMessageTextCommand(userNameValidationVisualizer.get().toSting(userNameValidation))
            return
        }

        // Password validation
        val passwordValidation = model.validatePassword(password.value!!)
        if(passwordValidation != PasswordValidationResult.SUCCESS) {
            _command.value = SetPasswordFocusCommand()
            _command.value = ShowMessageTextCommand(passwordValidationVisualizer.get().toSting(passwordValidation))
            return
        }

        _command.value = HideSoftKeyboardCommand()

        // Authentication
        auth(userName.value!!, password.value!!)
    }

    fun onMoveToSignUpClick() {
        _command.value = NavigateToSignUpCommand()
    }

    fun onQrCodeClick() {
        _command.value = NavigateToQrCodeCommand()
    }

    fun onQrCodeCameraPermissionsDenied() {
        _command.value = ShowMessageResCommand(R.string.qr_no_camera_permissions)
    }

    private fun tryToGetPassFromClipboard() {
//        val clipPassword = model.getPasswordFromClipboard()
//
//        if(clipPassword != null) {
//            _pastePasswordVisibility.value = View.VISIBLE
//            _pastePasswordText.value = appContext.resources.getFormattedString(R.string.paste_something, clipPassword)
//        } else {
//            _pastePasswordVisibility.value = View.GONE
//            _pastePasswordText.value = ""
//        }
    }

    private fun tryToGetQrCode() {
        dataPass.getQrCode()
            ?.let {
                userName.value = it.userName
                password.value = it.password
            }
    }

    private fun auth(userName: String, password: String) {
        launch {
            var authSuccess = true

            _command.value = SetLoadingVisibilityCommand(true)

            try {
                model.auth(userName, password)
            } catch (ex: Exception) {
                Timber.e(ex)
                authSuccess = false
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }

            if(authSuccess) {
                _command.value = NavigateForwardCommand()
            } else {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }
}