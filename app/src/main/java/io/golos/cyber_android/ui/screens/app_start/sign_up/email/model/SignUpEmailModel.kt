package io.golos.cyber_android.ui.screens.app_start.sign_up.email.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface SignUpEmailModel : ModelBase {
    fun isEmailValid(email: String): Boolean
}