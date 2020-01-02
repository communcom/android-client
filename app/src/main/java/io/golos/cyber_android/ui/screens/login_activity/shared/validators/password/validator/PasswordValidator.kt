package io.golos.cyber_android.ui.screens.login_activity.shared.validators.password.validator

interface PasswordValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : PasswordValidationResult
}