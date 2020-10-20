package io.golos.domain.validation.user_name

import io.golos.utils.helpers.isMatch
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
            validatedValue.isEmpty() -> UserNameValidationResult.IS_EMPTY

            !"""^[a-z0-9-.]+$""".isMatch(validatedValue) -> UserNameValidationResult.INVALID_CHARACTER

            !"""^[a-z]{1}""".isMatch(validatedValue) -> UserNameValidationResult.START_WITH_LETTER

            validatedValue.length < minLen -> UserNameValidationResult.IS_TOO_SHORT

            validatedValue.length > maxLen -> UserNameValidationResult.IS_TOO_LONG

            validatedValue.contains("--") -> UserNameValidationResult.TWO_DASH_IN_ROW

            validatedValue.contains("..") -> UserNameValidationResult.TWO_DOT_IN_ROW

            validatedValue.contains(".-") || validatedValue.contains("-.") -> UserNameValidationResult.DASH_DOT_IN_ROW

            !"""[a-z0-9]{1}$""".isMatch(validatedValue) -> UserNameValidationResult.END_WITH_LETTER_OR_DIGIT

            else -> UserNameValidationResult.SUCCESS
        }
}