package io.golos.cyber_android.ui.screens.wallet_shared.amount_validator

import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult

interface AmountValidator {
    fun validate(amount: Double?, maxAmount: Double, fee: Double): AmountValidationResult
}