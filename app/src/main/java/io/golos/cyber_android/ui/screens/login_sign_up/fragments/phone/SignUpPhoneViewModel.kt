package io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone

import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import javax.inject.Inject

class SignUpPhoneViewModel
@Inject
constructor(
    private val analyticsFacade: AnalyticsFacade
): SignUpScreenViewModelBase() {
    init {
        analyticsFacade.openScreen112()
    }

    override fun validate(field: String, minFieldCount: Int): Boolean {
        return field.length - 1 >= minFieldCount
    }
}