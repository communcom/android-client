package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpEmailVerificationModelImpl
@Inject
constructor() : ModelBaseImpl(), SignUpEmailVerificationModel {
    override fun isCodeValid(code: String): Boolean = code.length >= 6
}