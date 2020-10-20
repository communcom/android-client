package io.golos.cyber_android.ui.screens.app_start.sign_in.username.model.password_validator.validator

enum class PasswordValidationResult {
    SUCCESS,

    LEN_IS_TOO_SHORT,
    LEN_IS_TOO_LONG,

    INVALID_CHARACTER
}