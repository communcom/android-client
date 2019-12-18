package io.golos.cyber_android.ui.screens.login_sign_in_username.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidator
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidator
import javax.inject.Inject

class SignInModelImpl
@Inject
constructor(
    private val userNameValidator: UserNameValidator,
    private val passwordValidator: PasswordValidator
) : ModelBaseImpl(),
    SignInModel {

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
}
