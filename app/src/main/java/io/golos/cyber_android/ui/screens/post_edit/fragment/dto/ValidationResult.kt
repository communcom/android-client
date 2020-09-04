package io.golos.cyber_android.ui.screens.post_edit.fragment.dto

/**
 * Post validation result
 */
enum class ValidationResult {
    SUCCESS,

    ERROR_POST_IS_EMPTY,
    ERROR_POST_IS_TOO_LONG,
    ERROR_TITLE_IS_EMPTY
}