package io.golos.cyber_android.ui.screens.login_sign_in_username.model

import dagger.Lazy
import io.golos.cyber_android.ui.shared.clipboard.ClipboardUtils
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.password.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.password.validator.PasswordValidator
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidator
import io.golos.use_cases.auth.AuthUseCase
import javax.inject.Inject

class SignInUserNameModelImpl
@Inject
constructor(
    private val userNameValidator: UserNameValidator,
    private val passwordValidator: PasswordValidator,
    private val clipboardUtils: ClipboardUtils,
    private val authUseCase: Lazy<AuthUseCase>
) : ModelBaseImpl(),
    SignInUserNameModel {

    override val minUserNameLen: Int
        get() = userNameValidator.minLen

    override val maxUserNameLen: Int
        get() = userNameValidator.maxLen

    override val minPasswordLen: Int
        get() = passwordValidator.minLen

    override val maxPasswordLen: Int
        get() = passwordValidator.maxLen

    override fun validateUserName(userName: String): UserNameValidationResult = userNameValidator.validate(userName)

    override fun validatePassword(password: String): PasswordValidationResult = passwordValidator.validate(password)

    override fun getPasswordFromClipboard(): String? =
        clipboardUtils.getPlainText()
            ?.let {
            if (passwordValidator.validate(it) == PasswordValidationResult.SUCCESS) it else null
        }

    override suspend fun auth(userName: String, password: String) = authUseCase.get().auth(userName, password)
}
