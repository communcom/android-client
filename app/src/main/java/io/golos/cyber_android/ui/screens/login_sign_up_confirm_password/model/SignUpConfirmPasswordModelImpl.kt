package io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpConfirmPasswordModelImpl
@Inject
constructor(
    appContext: Context,
    override val passwordValidator: PasswordValidator,
    private val dataPass: LoginActivityFragmentsDataPass
) : SignUpCreatePasswordModel,
    ModelBaseImpl() {

    override val screenTitle: String = appContext.getString(R.string.confirm_password)
    override val passwordHint: String = appContext.getString(R.string.reenter_your_password)

    override fun savePassword(password: String) {
        //dataPass.putPassword(password)
    }
}