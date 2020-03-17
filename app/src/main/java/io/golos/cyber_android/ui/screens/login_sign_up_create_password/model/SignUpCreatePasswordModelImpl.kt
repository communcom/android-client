package io.golos.cyber_android.ui.screens.login_sign_up_create_password.model

import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpCreatePasswordModelImpl
@Inject
constructor(
    override val passwordValidator: PasswordValidator,
    private val dataPass: LoginActivityFragmentsDataPass
) : SignUpCreatePasswordModel,
    ModelBaseImpl() {

    override fun savePassword(password: String) {
        dataPass.putPassword(password)
    }
}