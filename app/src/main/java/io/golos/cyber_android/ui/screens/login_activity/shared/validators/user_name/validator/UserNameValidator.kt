package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator

interface UserNameValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : UserNameValidationResult
}