package io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.validator

interface PasswordValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : PasswordValidationResult
}