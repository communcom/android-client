package io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator

import java.util.regex.Pattern
import javax.inject.Inject

class PasswordValidatorImpl
@Inject
constructor() : PasswordValidator {
    override val minLen: Int
        get() = 8
    override val maxLen: Int
        get() = 52

    override fun validate(validatedValue: String): PasswordValidationResult =
        when {
            validatedValue.length < minLen -> PasswordValidationResult.LEN_IS_TOO_SHORT
            validatedValue.length > maxLen -> PasswordValidationResult.LEN_IS_TOO_LONG

            !"^[a-zA-Z0-9]+$".isMatch(validatedValue) -> PasswordValidationResult.INVALID_CHARACTER

            else -> PasswordValidationResult.SUCCESS
        }

    private fun String.isMatch(s: String): Boolean = Pattern.compile(this).matcher(s).find()
}