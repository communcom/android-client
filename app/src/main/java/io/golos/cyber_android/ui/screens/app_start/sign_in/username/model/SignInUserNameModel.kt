package io.golos.cyber_android.ui.screens.app_start.sign_in.username.model

import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator.PasswordValidationResult
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.validation.user_name.UserNameValidationResult

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