package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.widgets.pin.Digit

interface PinCodeAuthModel: ModelBase {
    val isFingerprintAuthPossible: Boolean

    suspend fun initModel()

    fun processDigit(digit: Digit): PinCodeValidationResult

    fun reset()
}