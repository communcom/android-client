package io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator

import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordValidationCase

interface PasswordValidator {
    val minLen: Int
    val maxLen: Int

    /**
     * @return set of valid cases
     */
    fun validate(pass: String): List<PasswordValidationCase>

    fun getExplanation(case: PasswordValidationCase): String

    fun getFirstInvalidCase(validCases: List<PasswordValidationCase>): PasswordValidationCase?

    fun hasInvalidCharacters(pass: String): Boolean
}