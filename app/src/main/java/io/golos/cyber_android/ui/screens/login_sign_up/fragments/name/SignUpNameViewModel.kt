package io.golos.cyber_android.ui.screens.login_sign_up.fragments.name

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import javax.inject.Inject

class SignUpNameViewModel
@Inject
constructor(
    private val appContext: Context
) : SignUpScreenViewModelBase() {

    private val userNameValidator by lazy { UserNameValidatorImpl() }       // Use injection here, after refactoring!!!

    val maxUserNameLen get() = userNameValidator.maxLen

    private val _validationResult = MutableLiveData<ValidationResult>(ValidationResult(true, null))
    val validationResult: LiveData<ValidationResult> = _validationResult

    override fun validate(field: String): Boolean = userNameValidator.validate(field) == UserNameValidationResult.SUCCESS

    override fun onFieldChanged(field: String) {
        super.onFieldChanged(field)

        this.field = field.trim()

        _validationResult.value = when(userNameValidator.validate(field)) {
            UserNameValidationResult.SUCCESS -> ValidationResult(true, null)

            UserNameValidationResult.LEN_IS_TOO_SHORT,
            UserNameValidationResult.LEN_IS_TOO_LONG ->
                ValidationResult(
                    false,
                    appContext.resources.getFormattedString(
                        R.string.username_validation_len,
                        userNameValidator.minLen,
                        userNameValidator.maxLen))

            UserNameValidationResult.CANT_START_WITH_DOT ->
                ValidationResult(false, appContext.getString(R.string.username_validation_dot_start))

            UserNameValidationResult.CANT_END_WITH_DOT ->
                ValidationResult(false, appContext.getString(R.string.username_validation_dot_end))

            UserNameValidationResult.CANT_CONTAIN_TWO_DOT_IN_ROW ->
                ValidationResult(false, appContext.getString(R.string.username_validation_two_dots))

            UserNameValidationResult.CANT_START_WITH_DASH ->
                ValidationResult(false, appContext.getString(R.string.username_validation_dash_start))

            UserNameValidationResult.CANT_END_WITH_DASH ->
                ValidationResult(false, appContext.getString(R.string.username_validation_dash_end))

            UserNameValidationResult.CANT_CONTAIN_TWO_DASH_IN_ROW ->
                ValidationResult(false, appContext.getString(R.string.username_validation_two_dashes))

            UserNameValidationResult.CANT_CONTAIN_DASH_DOT_IN_ROW ->
                ValidationResult(false, appContext.getString(R.string.username_validation_dot_dashes))

            UserNameValidationResult.INVALID_CHARACTER ->
                ValidationResult(false, appContext.getString(R.string.username_validation_invalid_characters))
        }
    }
}