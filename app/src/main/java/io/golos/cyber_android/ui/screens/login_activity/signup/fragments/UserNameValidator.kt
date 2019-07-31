package io.golos.cyber_android.ui.screens.login_activity.signup.fragments

import java.util.regex.Pattern

class UserNameValidator {
    /**
     * @return false - is not valid
     */
    fun isValid(userName: String): Boolean {
        if("[.-]$".isMatch(userName)) {
            return false
        }

        if("[.-].*[.-]".isMatch(userName)) {
            return false
        }

        if("^[.-]".isMatch(userName)) {
            return false
        }

        if("^[.-]+$".isMatch(userName)) {
            return false
        }

        return true
    }

    private fun String.isMatch(s: String): Boolean = Pattern.compile(this).matcher(s).find()
}