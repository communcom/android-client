package io.golos.cyber_android.ui.screens.wallet.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.utils.IdUtil
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class WalletModelImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    override val pageSize: Int,
    @Named(Clarification.WALLET_BALANCE)
    private val sourceBalance: List<WalletCommunityBalanceRecordDomain>,
    private val dispatchersProvider: DispatchersProvider,
    private val walletRepository: WalletRepository,
    private val sendPointsDataSource: SendPointsDataSource,
    private val historyDataSource: HistoryDataSource
) : ModelBaseImpl(),
    WalletModel {

    override lateinit var balance: List<WalletCommunityBalanceRecordDomain>

    override val totalBalance: Double
        get() = balance.sumByDouble { it.communs ?: 0.0 }

    override val sendPointItems: LiveData<List<VersionedListItem>>
        get() = sendPointsDataSource.items

    override val historyItems: LiveData<List<VersionedListItem>>
        get() = historyDataSource.items

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

            result.add(MyPointsListItem(IdUtil.generateLongId(), 0, false, false, true, communItem))

            result.addAll(
                balance
                    .filter { it.communityId != GlobalConstants.COMMUN_CODE }
                    .map { MyPointsListItem(IdUtil.generateLongId(), 0, false, false, false, it) }
            )

            result
        }

    override suspend fun loadSendPointsPage() = sendPointsDataSource.loadPage()

    override suspend fun retrySendPointsPage() = sendPointsDataSource.retry()

    override suspend fun clearSendPoints() = sendPointsDataSource.clear()

    override suspend fun loadHistoryPage() = historyDataSource.loadPage()

    override suspend fun retryHistoryPage() = historyDataSource.retry()

    override suspend fun clearHistory() = historyDataSource.clear()
}