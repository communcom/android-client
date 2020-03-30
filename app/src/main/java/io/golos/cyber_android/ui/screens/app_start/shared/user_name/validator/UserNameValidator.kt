package io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator

interface UserNameValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : UserNameValidationResult
}