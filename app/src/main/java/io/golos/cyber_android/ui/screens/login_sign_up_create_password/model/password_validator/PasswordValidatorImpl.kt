package io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import javax.inject.Inject

class PasswordValidatorImpl
@Inject
constructor(
    private val appContext: Context
) : PasswordValidator {
    override val minLen: Int = 8
    override val maxLen: Int = 128

    /**
     * @return set of valid cases
     */
    override fun validate(pass: String): List<PasswordValidationCase> {
        val result = mutableListOf<PasswordValidationCase>()

        if(pass.length >= minLen) {
            result.add(PasswordValidationCase.MIN_LEN)
        }

        var checkLower = true
        var checkUpper = true
        var checkDigit = true

        for(i in pass.indices) {
            if(checkLower && pass[i].isLowerCase()) {
                result.add(PasswordValidationCase.LOWER)
                checkLower = false
            }

            if(checkUpper && pass[i].isUpperCase()) {
                result.add(PasswordValidationCase.UPPER)
                checkUpper = false
            }

            if(checkDigit && pass[i].isDigit()) {
                result.add(PasswordValidationCase.DIGIT)
                checkDigit = false
            }

            if(!checkLower && !checkUpper && !checkDigit) {
                break
            }
        }

        return result
    }

    override fun getExplanation(case: PasswordValidationCase): String =
        when(case) {
            PasswordValidationCase.LOWER -> appContext.getString(R.string.password_validation_lower_explanation)
            PasswordValidationCase.UPPER -> appContext.getString(R.string.password_validation_upper_explanation)
            PasswordValidationCase.DIGIT -> appContext.getString(R.string.password_validation_number_explanation)
            PasswordValidationCase.MIN_LEN ->
                appContext.resources.getFormattedString(R.string.password_validation_min_len_explanation, minLen)
        }

    override fun getFirstInvalidCase(validCases: List<PasswordValidationCase>): PasswordValidationCase? =
        PasswordValidationCase.values()
            .subtract(validCases)
            .firstOrNull()
}