package io.golos.cyber_android.ui.screens.wallet.model

import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies

data class CurrencyBalance(
    val balance: Double,
    val currency: Currencies
)