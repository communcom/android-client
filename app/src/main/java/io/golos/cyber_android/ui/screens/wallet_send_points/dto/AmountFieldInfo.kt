package io.golos.cyber_android.ui.screens.wallet_send_points.dto

import androidx.annotation.StringRes

data class AmountFieldInfo (
    @StringRes val hintResId: Int,
    val decimalPointsQuantity: Int
)
