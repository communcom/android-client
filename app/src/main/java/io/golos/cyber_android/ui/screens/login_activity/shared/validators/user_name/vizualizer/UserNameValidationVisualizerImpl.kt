package io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.vizualizer

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.getFormattedString
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidator
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class UserNameValidationVisualizerImpl
@Inject
constructor(
    private val appContext: Context,
    private val validator: UserNameValidator
) : UserNameValidationVisualizer {

    override fun toSting(validationResult: UserNameValidationResult) : String =
        when(validationResult) {
            UserNameValidationResult.LEN_IS_TOO_SHORT ->
                appContext.resources.getFormattedString(R.string.user_name_short_new, validator.minLen)

            UserNameValidationResult.LEN_IS_TOO_LONG ->
                appContext.resources.getFormattedString(R.string.user_name_long_new, validator.maxLen)

            UserNameValidationResult.CANT_START_WITH_DOT ->
                appContext.resources.getString(R.string.user_name_begin_not_correct_dot)

            UserNameValidationResult.CANT_END_WITH_DOT ->
                appContext.resources.getString(R.string.user_name_end_not_correct_dot)

            UserNameValidationResult.CANT_CONTAIN_TWO_DOT_IN_ROW ->
                appContext.resources.getString(R.string.user_name_two_dot)

            UserNameValidationResult.CANT_START_WITH_DASH ->
                appContext.resources.getString(R.string.user_name_begin_not_correct)

            UserNameValidationResult.CANT_END_WITH_DASH ->
                appContext.resources.getString(R.string.user_name_end_not_correct)

            UserNameValidationResult.CANT_CONTAIN_TWO_DASH_IN_ROW ->
                appContext.resources.getString(R.string.user_name_two_dash)

            UserNameValidationResult.CANT_CONTAIN_DASH_DOT_IN_ROW ->
                appContext.resources.getString(R.string.user_name_two_dash_dot)

            UserNameValidationResult.INVALID_CHARACTER ->
                appContext.resources.getString(R.string.user_name_contain_characters)

            else -> throw UnsupportedOperationException("This value is not supported: $validationResult")
        }
}