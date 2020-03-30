package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface SignUpVerificationModel : ModelBase {
    val code: String

    /**
     * @return true is the code is valid
     */
    fun updateCode(newCode: String): Boolean
}