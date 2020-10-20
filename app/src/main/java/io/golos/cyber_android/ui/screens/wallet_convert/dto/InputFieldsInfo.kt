package io.golos.cyber_android.ui.screens.wallet_convert.dto

import androidx.annotation.StringRes

data class InputFieldsInfo(
    @StringRes val buyerHint: Int,
    @StringRes val sellerHint: Int,
    val buyerDecimalPoints: Int,
    val sellerDecimalPoints: Int
)