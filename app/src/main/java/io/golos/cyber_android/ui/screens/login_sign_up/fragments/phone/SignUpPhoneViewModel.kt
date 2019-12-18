package io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone

import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import javax.inject.Inject

class SignUpPhoneViewModel
@Inject
constructor(): SignUpScreenViewModelBase() {
    override fun validate(field: String, minFieldCount: Int): Boolean {
        return field.length - 1 >= minFieldCount
    }
}