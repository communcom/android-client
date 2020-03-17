package io.golos.cyber_android.ui.screens.login_sign_up_create_password.model

import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface SignUpCreatePasswordModel : ModelBase {
    val passwordValidator: PasswordValidator

    val screenTitle: String
    val passwordHint: String

    fun savePassword(password: String)
}