package io.golos.cyber_android.ui.screens.login_sign_up_create_password.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordProcessingResult
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpCreatePasswordModelImpl
@Inject
constructor(
    appContext: Context,
    override val passwordValidator: PasswordValidator,
    private val dataPass: LoginActivityFragmentsDataPass
) : SignUpCreatePasswordModel,
    ModelBaseImpl() {

    override val screenTitle: String = appContext.getString(R.string.create_password)
    override val passwordHint: String = appContext.getString(R.string.enter_your_password)

    override suspend fun processPassword(password: String): PasswordProcessingResult {
        dataPass.putPassword(password)
        return PasswordProcessingResult.SUCCESS
    }
}