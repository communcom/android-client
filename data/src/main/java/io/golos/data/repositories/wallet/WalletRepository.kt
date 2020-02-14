package io.golos.data.repositories.wallet

import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain

interface WalletRepository {
    suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain>

    suspend fun getTransferHistory(
        offset: Int,
        limit: Int,
        communityId: String = GlobalConstants.ALL_COMMUNITIES_CODE
    ): List<WalletTransferHistoryRecordDomain>
}