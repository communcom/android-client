package io.golos.cyber_android.ui.screens.wallet_point.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
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
    @Named(Clarification.COMMUNITY_ID)
    private var currentCommunityId: String,
    @Named(Clarification.WALLET_POINT_BALANCE)
    private val sourceBalance: List<WalletCommunityBalanceRecordDomain>,
    private val dispatchersProvider: DispatchersProvider,
    private val walletRepository: WalletRepository,
    private val sendPointsDataSource: SendPointsDataSource,
    private val historyDataSource: HistoryDataSource
) : ModelBaseImpl(), WalletPointModel {

    init {
        historyDataSource.communityId = currentCommunityId
    }

    override lateinit var balance: List<WalletCommunityBalanceRecordDomain>

    override lateinit var currentBalanceRecord: WalletCommunityBalanceRecordDomain

    override val title: String?
        get() = currentBalanceRecord.communityName

    override val balanceInPoints: Double
        get() = currentBalanceRecord.points

    override val holdPoints: Double
        get() = currentBalanceRecord.frozenPoints ?: 0.0

    override val balanceInCommuns: Double
        get() = currentBalanceRecord.communs ?: 0.0

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
        currentBalanceRecord = balance.first { it.communityId == currentCommunityId }
    }

    override fun getCarouselStartData() =
        CarouselStartData(
            startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
            items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
        )

    override fun switchBalanceRecord(communityId: String): Boolean =
        if(communityId == currentCommunityId) {
            false
        } else {
            currentCommunityId = communityId
            currentBalanceRecord = balance.first { it.communityId == currentCommunityId }

            historyDataSource.communityId = communityId
            true
        }

    override suspend fun loadSendPointsPage() = sendPointsDataSource.loadPage()

    override suspend fun retrySendPointsPage() = sendPointsDataSource.retry()

    override suspend fun clearSendPoints() = sendPointsDataSource.clear()

    override suspend fun loadHistoryPage() = historyDataSource.loadPage()

    override suspend fun retryHistoryPage() = historyDataSource.retry()

    override suspend fun clearHistory() = historyDataSource.clear()
}