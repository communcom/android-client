package io.golos.cyber_android.ui.screens.wallet.data.enums

enum class Currencies(val currencyName: String, val coefficient: Double) {
    USD("usd", 0.015),
    COMMUN("commun", 1.0);

    companion object {
        fun getCurrency(currencyName: String): Currencies? = values().find {
            it.currencyName == currencyName
        }
    }
}