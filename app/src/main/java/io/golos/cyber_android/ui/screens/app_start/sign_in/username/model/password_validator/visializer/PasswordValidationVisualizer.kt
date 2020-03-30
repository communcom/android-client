package io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.visializer

import io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator.PasswordValidationResult

interface PasswordValidationVisualizer {
    fun toSting(validationResult: PasswordValidationResult) : String
}