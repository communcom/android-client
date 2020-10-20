package io.golos.cyber_android.ui.screens.app_start.shared.user_name.vizualizer

import io.golos.domain.validation.user_name.UserNameValidationResult

interface UserNameValidationVisualizer {
    fun toSting(validationResult: UserNameValidationResult) : String
}