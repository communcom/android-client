package io.golos.data.repositories.wallet

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryTransferType
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbol
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.data.mappers.mapToWalletCommunityBalanceRecordDomain
import io.golos.data.mappers.mapToWalletTransferHistoryRecordDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.repositories.RepositoryBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.UserKeyType
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.dto.WalletTransferHistoryRecordDomain
import io.golos.domain.repositories.CurrentUserRepository
import javax.inject.Inject

class WalletRepositoryImpl
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val currentUserRepository: CurrentUserRepository,
    private val commun4j: Commun4j,
    private val userKeyStore: UserKeyStore
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    WalletRepository {

    override suspend fun getBalance(): List<WalletCommunityBalanceRecordDomain> {
        val callResult = apiCall { commun4j.getBalance(CyberName(currentUserRepository.userId.userId)) }

        if(callResult.all { it.symbol.value != GlobalConstants.COMMUN_CODE }) {
            apiCallChain {
                commun4j.openBalance(
                    symbol = CyberSymbol("4,CMN"),
                    ramPayer = CyberName(currentUserRepository.userId.userId),
                    owner = CyberName(currentUserRepository.userId.userId),
                    bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                    key = userKeyStore.getKey(UserKeyType.ACTIVE)
                )
            }
        }

        return callResult.balances.map { it.mapToWalletCommunityBalanceRecordDomain() }
    }

    override suspend fun getTransferHistory(offset: Int, limit: Int, communityId: String): List<WalletTransferHistoryRecordDomain> =
        apiCall {commun4j.getTransferHistory(
            userId = CyberName(currentUserRepository.userId.userId),
            direction = TransferHistoryDirection.ALL,
            transferType = TransferHistoryTransferType.ALL,
            symbol = CyberSymbolCode(communityId),
            rewards = "all",
            limit = limit,
            offset = offset
        )}
        .items.map { it.mapToWalletTransferHistoryRecordDomain() }
}