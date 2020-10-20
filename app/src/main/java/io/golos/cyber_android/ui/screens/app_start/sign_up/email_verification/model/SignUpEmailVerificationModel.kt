package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface SignUpEmailVerificationModel : ModelBase {
    fun isCodeValid(code: String): Boolean
}