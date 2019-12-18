package io.golos.cyber_android.ui.screens.login_activity.validators.user_name.vizualizer

import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidationResult

interface UserNameValidationVisualizer {
    fun toSting(validationResult: UserNameValidationResult) : String
}