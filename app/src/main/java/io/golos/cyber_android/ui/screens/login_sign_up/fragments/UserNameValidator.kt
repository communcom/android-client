package io.golos.cyber_android.ui.screens.login_sign_up.fragments

import io.golos.cyber_android.R
import java.util.regex.Pattern


class UserNameValidator {

    private var validateErrorMessageResId: Int = -1

    /**
     * @return false - is not valid
     */
    fun isValid(userName: String): Boolean {
        if ("[.-]$".isMatch(userName)) {
            validateErrorMessageResId = R.string.user_name_end_not_correct
            return false
        }

        if ("[.-].*[.-]".isMatch(userName)) {
            validateErrorMessageResId = R.string.user_name_two_dot
            return false
        }

        if ("^[.-]".isMatch(userName)) {
            validateErrorMessageResId = R.string.user_name_begin_not_correct
            return false
        }

        if ("^[.-]+$".isMatch(userName)) {
            validateErrorMessageResId = R.string.user_name_contain_characters
            return false
        }
        val count = userName.count()
        if (count < USER_NAME_MIN_LENGTH) {
            validateErrorMessageResId = R.string.user_name_short
            return false
        }
        if(count > USER_NAME_MAX_LENGTH){
            validateErrorMessageResId = R.string.user_name_short
            return false
        }
        validateErrorMessageResId = -1
        return true
    }

    /**
     * After call [isValid] can get id error message, if user name validated than id is equal to -1
     */
    fun getValidateErrorMessage() = validateErrorMessageResId

    private fun String.isMatch(s: String): Boolean = Pattern.compile(this).matcher(s).find()

    private companion object {

        /**
         * Min count characters allowable in name
         */
        private const val USER_NAME_MIN_LENGTH = 5

        /**
         * Max count characters allowable in name
         */
        private const val USER_NAME_MAX_LENGTH = 32

    }
}