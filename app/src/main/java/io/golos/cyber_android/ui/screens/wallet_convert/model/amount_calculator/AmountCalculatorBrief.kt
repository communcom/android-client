package io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator

interface AmountCalculatorBrief {
    val sellAmount: Double?
    val buyAmount: Double?

    /**
     * In sold coins
     */
    val fee: Double

    /**
     * How many points is in one Commun
     */
    val pointsInCommun: Double

    /**
     * @return true in case of success
     */
    fun updateSellAmount(value: String?): Boolean

    /**
     * @return true in case of success
     */
    fun updateBuyAmount(value: String?): Boolean
}