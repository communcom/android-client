package io.golos.data.repositories.wallet

import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain

interface WalletRepository {
    suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain>

    suspend fun getTransferHistory(
        offset: Int,
        limit: Int,
        communityId: CommunityIdDomain = CommunityIdDomain(GlobalConstants.ALL_COMMUNITIES_CODE)
    ): List<WalletTransferHistoryRecordDomain>

    suspend fun sendToUser(toUser: UserIdDomain, amount: Double, communityId: CommunityIdDomain)

    suspend fun convertPointsToCommun(amount: Double, communityId: CommunityIdDomain)

    suspend fun convertCommunToPoints(amount: Double, communityId: CommunityIdDomain)
}