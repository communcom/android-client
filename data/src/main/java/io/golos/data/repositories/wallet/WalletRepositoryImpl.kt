package io.golos.data.repositories.wallet

import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.repositories.RepositoryBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.CurrentUserRepository
import javax.inject.Inject

class WalletRepositoryImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val currentUserRepository: CurrentUserRepository,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    WalletRepository {

    override suspend fun getTotalBalanceInCommuns(): Double =
        apiCall { commun4j.getBalance(CyberName(currentUserRepository.userId.userId)) }
        .balances.sumByDouble { it.price ?: 0.0 }
}