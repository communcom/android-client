package io.golos.cyber_android.ui.screens.login_activity.validators.password.visializer

import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidationResult

interface PasswordValidationVisualizer {
    fun toSting(validationResult: PasswordValidationResult) : String
}