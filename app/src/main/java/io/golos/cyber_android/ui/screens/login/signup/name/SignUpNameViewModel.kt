package io.golos.cyber_android.ui.screens.login.signup.name

import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.ui.screens.login.signup.BaseSignUpScreenViewModel

class SignUpNameViewModel : BaseSignUpScreenViewModel() {

    companion object {
        /**
         * Exact length of the username that is valid
         */
        const val USERNAME_LENGTH = 12
    }

    override fun validate(field: String): Boolean {
        return field.length == USERNAME_LENGTH && try {
            field.toCyberName()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }
}