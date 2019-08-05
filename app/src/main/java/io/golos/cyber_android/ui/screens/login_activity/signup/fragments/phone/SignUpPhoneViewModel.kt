package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.phone

import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpScreenViewModelBase
import javax.inject.Inject

class SignUpPhoneViewModel
@Inject
constructor(): SignUpScreenViewModelBase() {
    override fun validate(field: String): Boolean {
        return field.length > 10
    }
}