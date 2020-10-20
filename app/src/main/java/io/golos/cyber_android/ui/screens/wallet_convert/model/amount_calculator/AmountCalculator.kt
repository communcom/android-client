package io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator

import io.golos.domain.dto.CommunityIdDomain

interface AmountCalculator : AmountCalculatorBrief {
    /**
     * [isInverted] false if we sell points and buy Commun
     */
    suspend fun init(points: Double, communs: Double, isInverted: Boolean, communityId: CommunityIdDomain)

    fun inverse()
}