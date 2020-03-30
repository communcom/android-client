package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model

import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface SignUpCreatePasswordModel : ModelBase {
    val passwordValidator: PasswordValidator

    val screenTitle: String
    val passwordHint: String

    fun processPassword(password: String)
}