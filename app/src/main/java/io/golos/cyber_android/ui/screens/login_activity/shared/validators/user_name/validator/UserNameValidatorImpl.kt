package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator

import java.util.regex.Pattern
import javax.inject.Inject

class UserNameValidatorImpl
@Inject
constructor() : UserNameValidator {
    override val minLen: Int
        get() = 3

    override val maxLen: Int
        get() = 32

    override fun validate(validatedValue: String): UserNameValidationResult =
        when {
            validatedValue.length < minLen -> UserNameValidationResult.LEN_IS_TOO_SHORT
            validatedValue.length > maxLen -> UserNameValidationResult.LEN_IS_TOO_LONG

            validatedValue.startsWith(".") -> UserNameValidationResult.CANT_START_WITH_DOT
            validatedValue.endsWith(".") -> UserNameValidationResult.CANT_END_WITH_DOT
            validatedValue.contains("..") -> UserNameValidationResult.CANT_CONTAIN_TWO_DOT_IN_ROW

            validatedValue.startsWith("-") -> UserNameValidationResult.CANT_START_WITH_DASH
            validatedValue.endsWith("-") -> UserNameValidationResult.CANT_END_WITH_DASH
            validatedValue.contains("--") -> UserNameValidationResult.CANT_CONTAIN_TWO_DASH_IN_ROW

            validatedValue.contains(".-") || validatedValue.contains("-.") -> UserNameValidationResult.CANT_CONTAIN_DASH_DOT_IN_ROW

            !"^[a-z0-9.-]+$".isMatch(validatedValue) -> UserNameValidationResult.INVALID_CHARACTER

            else -> UserNameValidationResult.SUCCESS
        }

    private fun String.isMatch(s: String): Boolean = Pattern.compile(this).matcher(s).find()
}