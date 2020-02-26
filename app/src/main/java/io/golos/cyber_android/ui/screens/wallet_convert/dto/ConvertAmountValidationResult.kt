package io.golos.cyber_android.ui.screens.wallet_convert.dto

import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult

data class ConvertAmountValidationResult (
    val sellAmount: AmountValidationResult,
    val buyAmount: AmountValidationResult,
    val isValid: Boolean
)