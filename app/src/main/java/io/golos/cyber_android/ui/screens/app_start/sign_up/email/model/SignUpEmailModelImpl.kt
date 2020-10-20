package io.golos.cyber_android.ui.screens.app_start.sign_up.email.model

import android.util.Patterns
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.utils.helpers.isMatch
import javax.inject.Inject

class SignUpEmailModelImpl
@Inject
constructor() : ModelBaseImpl(), SignUpEmailModel {

    override fun isEmailValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.isMatch(email)
}