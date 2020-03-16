package io.golos.cyber_android.ui.screens.login_sign_in_username.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.screens.login_shared.validators.password.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.validator.UserNameValidationResult

interface  SignInUserNameModel : ModelBase {
    val minUserNameLen: Int
    val maxUserNameLen: Int

    val minPasswordLen: Int
    val maxPasswordLen: Int

    fun validateUserName(userName: String): UserNameValidationResult

    fun validatePassword(password: String): PasswordValidationResult

    fun getPasswordFromClipboard(): String?

    suspend fun auth(userName: String, password: String)
}