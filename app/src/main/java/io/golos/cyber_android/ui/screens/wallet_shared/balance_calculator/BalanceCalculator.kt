package io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator

import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface BalanceCalculator {
    /**
     * Returns total balance in communs
     */
    fun getTotalBalance(balance: List<WalletCommunityBalanceRecordDomain>): Double
}