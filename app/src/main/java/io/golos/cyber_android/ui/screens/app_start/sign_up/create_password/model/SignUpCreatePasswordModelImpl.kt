package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.PasswordEntered
import javax.inject.Inject

class SignUpCreatePasswordModelImpl
@Inject
constructor(
    appContext: Context,
    override val passwordValidator: PasswordValidator,
    private val signUpCore: SignUpCoreView
) : SignUpCreatePasswordModel,
    ModelBaseImpl() {

    override val screenTitle: String = appContext.getString(R.string.create_password)
    override val passwordHint: String = appContext.getString(R.string.enter_your_password)

    override fun processPassword(password: String) = signUpCore.process(PasswordEntered(password))
}