package io.golos.cyber_android.ui.screens.app_start.sign_up.username.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.validation.user_name.UserNameValidationResult

interface SignUpNameModel : ModelBase {
    val maxUserNameLen: Int

    val userName: String

    fun updateUserName(newUserName: String): UserNameValidationResult
}