package io.golos.cyber_android.ui.screens.wallet.model

import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalletModelImpl
@Inject
constructor(
    private val sourceBalance: List<WalletCommunityBalanceRecordDomain>,
    private val dispatchersProvider: DispatchersProvider,
    private val walletRepository: WalletRepository
) : ModelBaseImpl(),
    WalletModel {

    private lateinit var balance: List<WalletCommunityBalanceRecordDomain>

    override val totalBalance: Double
        get() = balance.sumByDouble { it.communs ?: 0.0 }

    override suspend fun initBalance(needReload: Boolean) {
        balance = if(needReload) {
            walletRepository.getBalance()
        } else {
            sourceBalance
        }
    }

    override suspend fun getMyPointsItems(): List<MyPointsListItem> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            val communItem = balance.firstOrNull { it.communityId == GlobalConstants.COMMUN_CODE }
                ?: WalletCommunityBalanceRecordDomain(0.0, null, null, null, GlobalConstants.COMMUN_CODE, null, null)

            val result = mutableListOf<MyPointsListItem>()

            result.add(MyPointsListItem(IdUtil.generateLongId(), 0, true, communItem))

            result.addAll(
                balance
                    .filter { it.communityId != GlobalConstants.COMMUN_CODE }
                    .map { MyPointsListItem(IdUtil.generateLongId(), 0, false, it) }
            )

            result
        }
}