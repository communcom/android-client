package io.golos.data.repositories.wallet

import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain

interface WalletRepository {
    suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain>

    suspend fun getTransferHistory(offset: Int, limit: Int): List<WalletTransferHistoryRecordDomain>
}