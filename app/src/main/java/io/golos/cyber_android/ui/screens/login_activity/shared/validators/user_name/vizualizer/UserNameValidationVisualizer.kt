package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.vizualizer

import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult

interface UserNameValidationVisualizer {
    fun toSting(validationResult: UserNameValidationResult) : String
}