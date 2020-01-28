package io.golos.cyber_android.ui.screens.login_sign_up.fragments.name

import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import javax.inject.Inject

class SignUpNameViewModel
@Inject
constructor() : SignUpScreenViewModelBase() {

    private val userNameValidator by lazy { UserNameValidatorImpl() }       // Use injection here, after refactoring!!!

    val maxUserNameLen get() = userNameValidator.maxLen

    override fun validate(field: String): Boolean = userNameValidator.validate(field) == UserNameValidationResult.SUCCESS
}