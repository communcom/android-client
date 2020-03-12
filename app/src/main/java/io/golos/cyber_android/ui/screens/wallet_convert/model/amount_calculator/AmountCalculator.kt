package io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator

interface AmountCalculator : AmountCalculatorBrief {
    /**
     * [isInverted] false if we sell points and buy Commun
     */
    fun init(points: Double, communs: Double, isInverted: Boolean)

    fun inverse()
}