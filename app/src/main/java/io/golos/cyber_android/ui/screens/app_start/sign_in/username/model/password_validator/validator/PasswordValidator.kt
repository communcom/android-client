package io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator

interface PasswordValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : PasswordValidationResult
}