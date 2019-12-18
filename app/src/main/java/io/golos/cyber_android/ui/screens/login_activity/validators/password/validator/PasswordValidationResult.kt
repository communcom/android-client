package io.golos.cyber_android.ui.screens.login_activity.validators.password.validator

enum class PasswordValidationResult {
    SUCCESS,

    LEN_IS_TOO_SHORT,
    LEN_IS_TOO_LONG,

    INVALID_CHARACTER
}