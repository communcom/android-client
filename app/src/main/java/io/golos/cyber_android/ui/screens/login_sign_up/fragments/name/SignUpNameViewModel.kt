package io.golos.cyber_android.ui.screens.login_sign_up.fragments.name

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.validator.UserNameValidatorImpl
import io.golos.cyber_android.ui.screens.login_activity.shared.validators.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import javax.inject.Inject

class SignUpNameViewModel
@Inject
constructor(
    private val userNameValidationVisualizer: UserNameValidationVisualizer,
    private val analyticsFacade: AnalyticsFacade
) : SignUpScreenViewModelBase() {

    private val userNameValidator by lazy { UserNameValidatorImpl() }       // Use injection here, after refactoring!!!

    val maxUserNameLen get() = userNameValidator.maxLen

    private val _validationResult = MutableLiveData<ValidationResult>(ValidationResult(true, null))
    val validationResult: LiveData<ValidationResult> = _validationResult

    init {
        analyticsFacade.openScreen114()
    }

    override fun validate(field: String): Boolean = userNameValidator.validate(field) == UserNameValidationResult.SUCCESS

    override fun onFieldChanged(field: String) {
        super.onFieldChanged(field)

        this.field = field.trim()

        _validationResult.value = userNameValidator.validate(field).let {
            when(userNameValidator.validate(field)) {
                UserNameValidationResult.SUCCESS -> ValidationResult(true, null)
                else -> ValidationResult(false, userNameValidationVisualizer.toSting(it))
            }
        }
    }
}