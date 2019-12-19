package io.golos.cyber_android.ui.screens.login_sign_in_username.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidationResult

interface  SignInModel : ModelBase {
    val minUserNameLen: Int
    val maxUserNameLen: Int

    val minPasswordLen: Int
    val maxPasswordLen: Int

    fun validateUserName(userName: String): UserNameValidationResult

    fun validatePassword(password: String): PasswordValidationResult

    fun getPasswordFromClipboard(): String?
}