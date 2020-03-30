package io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator

enum class UserNameValidationResult {
    SUCCESS,

    IS_EMPTY,

    INVALID_CHARACTER,

    START_WITH_LETTER,

    IS_TOO_SHORT,

    IS_TOO_LONG,

    TWO_DASH_IN_ROW,

    TWO_DOT_IN_ROW,

    DASH_DOT_IN_ROW,

    END_WITH_LETTER_OR_DIGIT
}