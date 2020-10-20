package io.golos.cyber_android.ui.screens.wallet_shared.amount_validator

import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import javax.inject.Inject

class AmountValidatorImpl
@Inject
constructor() : AmountValidator {
    override fun validate(amount: Double?, maxAmount: Double, fee: Double): AmountValidationResult =
        when {
            amount == null -> AmountValidationResult.INVALID_VALUE
            amount == 0.0 -> AmountValidationResult.CANT_BE_ZERO
            amount > maxAmount - fee -> AmountValidationResult.TOO_LARGE
            else -> AmountValidationResult.SUCCESS
        }
}