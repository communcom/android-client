package io.golos.cyber_android.ui.screens.login.signup.name

import io.golos.cyber_android.ui.screens.login.signup.SignUpScreenViewModelBase

class SignUpNameViewModel : SignUpScreenViewModelBase() {

    companion object {
        /**
         * Exact length of the username that is valid
         */
        const val MIN_USERNAME_LENGTH = 1
        const val MAX_USERNAME_LENGTH = 32
    }

    override fun validate(field: String): Boolean {
        return field.length in (MIN_USERNAME_LENGTH .. MAX_USERNAME_LENGTH)
    }
}