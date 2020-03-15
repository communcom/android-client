package io.golos.cyber_android.ui.screens.wallet_point.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import javax.inject.Inject
import javax.inject.Named

class WalletPointModelImpl
@Inject
constructor(
    @Named(Clarification.PAGE_SIZE)
    override val pageSize: Int,
    private var currentCommunityId: CommunityIdDomain,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override var sourceBalance: List<WalletCommunityBalanceRecordDomain>,
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
        if(needReload) {
            sourceBalance = walletRepository.getBalance()
        }

        balance = sourceBalance.filter { it.communityId.code != GlobalConstants.COMMUN_CODE }
        currentBalanceRecord = balance.first { it.communityId == currentCommunityId }
    }

    override fun getCarouselStartData() =
        CarouselStartData(
            startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
            items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
        )

    override fun switchBalanceRecord(communityId: CommunityIdDomain): Boolean =
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