package io.golos.cyber_android.ui.screens.app_start.sign_up.username.model

import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.app_start.shared.user_name.validator.UserNameValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class SignUpNameModelImpl
@Inject
constructor(
    private val userNameValidator: UserNameValidator
) : ModelBaseImpl(), SignUpNameModel {
    override val maxUserNameLen: Int
        get() = userNameValidator.maxLen

    override var userName: String = ""

    override fun updateUserName(newUserName: String): UserNameValidationResult {
        userName = newUserName
        return userNameValidator.validate(newUserName)
    }
}