package io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.visializer

import io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.validator.PasswordValidationResult

interface PasswordValidationVisualizer {
    fun toSting(validationResult: PasswordValidationResult) : String
}