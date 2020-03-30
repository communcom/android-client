package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpVerificationModelImpl
@Inject
constructor() : ModelBaseImpl(), SignUpVerificationModel {
    override var code: String = ""

    /**
     * @return true is the code is valid
     */
    override fun updateCode(newCode: String): Boolean {
        code = newCode

        if(newCode.length != 4) {
            return false
        }

        newCode.forEach {
            if(!it.isDigit()) {
                return false
            }
        }

        return true
    }
}