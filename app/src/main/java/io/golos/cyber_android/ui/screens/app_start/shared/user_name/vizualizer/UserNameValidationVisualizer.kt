package io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer

import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidationResult

interface UserNameValidationVisualizer {
    fun toSting(validationResult: UserNameValidationResult) : String
}