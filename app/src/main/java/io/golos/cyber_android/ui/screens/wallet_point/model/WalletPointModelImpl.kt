package io.golos.cyber_android.ui.screens.wallet_point.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject
import javax.inject.Named

class WalletPointModelImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    override val pageSize: Int,
    @Named(Clarification.WALLET_POINT_BALANCE)
    private val sourceBalance: List<WalletCommunityBalanceRecordDomain>,
    private val dispatchersProvider: DispatchersProvider,
    private val walletRepository: WalletRepository,
    private val sendPointsDataSource: SendPointsDataSource,
    private val historyDataSource: HistoryDataSource

) : ModelBaseImpl(), WalletPointModel {

    override val sendPointItems: LiveData<List<VersionedListItem>>
        get() = sendPointsDataSource.items

    override val historyItems: LiveData<List<VersionedListItem>>
        get() = historyDataSource.items

    override suspend fun loadSendPointsPage() = sendPointsDataSource.loadPage()

    override suspend fun retrySendPointsPage() = sendPointsDataSource.retry()

    override suspend fun clearSendPoints() = sendPointsDataSource.clear()

    override suspend fun loadHistoryPage() = historyDataSource.loadPage()

    override suspend fun retryHistoryPage() = historyDataSource.retry()

    override suspend fun clearHistory() = historyDataSource.clear()
}