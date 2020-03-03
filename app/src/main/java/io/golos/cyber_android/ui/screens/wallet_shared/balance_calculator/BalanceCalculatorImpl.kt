package io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator

import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject

class BalanceCalculatorImpl
@Inject
constructor() : BalanceCalculator {
    override fun getTotalBalance(balance: List<WalletCommunityBalanceRecordDomain>): Double =
        balance.sumByDouble {
            if(it.communityId == GlobalConstants.COMMUN_CODE) {
                it.points
            } else {
                it.communs ?: 0.0
            }
        }
}