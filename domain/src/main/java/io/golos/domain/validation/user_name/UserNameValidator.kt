package io.golos.domain.validation.user_name

interface UserNameValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : UserNameValidationResult
}