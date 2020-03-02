package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator

enum class UserNameValidationResult {
    SUCCESS,

    LEN_IS_TOO_SHORT,
    LEN_IS_TOO_LONG,

    CANT_START_WITH_DOT,
    CANT_END_WITH_DOT,
    CANT_CONTAIN_TWO_DOT_IN_ROW,

    CANT_START_WITH_DASH,
    CANT_END_WITH_DASH,
    CANT_CONTAIN_TWO_DASH_IN_ROW,

    CANT_CONTAIN_DASH_DOT_IN_ROW,

    CANT_START_WITH_DIGIT,

    SEGMENT_IS_TOO_SHORT,

    INVALID_CHARACTER
}