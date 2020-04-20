package io.golos.domain.validation.user_name

import io.golos.domain.validation.user_name.UserNameValidationResult

interface UserNameValidator {
    val minLen: Int
    val maxLen: Int

    fun validate(validatedValue: String) : UserNameValidationResult
}