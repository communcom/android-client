package io.golos.data.repositories.wallet

import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface WalletRepository {
    suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain>
}