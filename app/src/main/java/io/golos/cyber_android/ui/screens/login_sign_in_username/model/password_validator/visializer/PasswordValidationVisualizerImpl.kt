package io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.visializer

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.password_validator.validator.PasswordValidator
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import javax.inject.Inject

class PasswordValidationVisualizerImpl
@Inject
constructor(
    private val appContext: Context,
    private val validator: PasswordValidator
) : PasswordValidationVisualizer {

    override fun toSting(validationResult: PasswordValidationResult): String =
        when(validationResult) {
            PasswordValidationResult.LEN_IS_TOO_SHORT ->
                appContext.resources.getFormattedString(R.string.password_name_short, validator.minLen)

            PasswordValidationResult.LEN_IS_TOO_LONG ->
                appContext.resources.getFormattedString(R.string.password_name_long, validator.maxLen)

            PasswordValidationResult.INVALID_CHARACTER -> appContext.resources.getString(R.string.password_contain_characters)

            else -> throw UnsupportedOperationException("This value is not supported: $validationResult")
        }
}