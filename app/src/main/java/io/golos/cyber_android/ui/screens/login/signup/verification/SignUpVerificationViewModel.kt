package io.golos.cyber_android.ui.screens.login.signup.verification

import io.golos.cyber_android.ui.screens.login.signup.BaseSignUpScreenViewModel

class SignUpVerificationViewModel: BaseSignUpScreenViewModel() {

    override fun validate(field: String): Boolean {
        return field.length == 4
    }
}