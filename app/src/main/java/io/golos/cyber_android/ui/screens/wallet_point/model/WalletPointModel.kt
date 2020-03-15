package io.golos.cyber_android.ui.screens.wallet_point.model
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface WalletPointModel : ModelBase {
    val sourceBalance: List<WalletCommunityBalanceRecordDomain>

    val title: String?

    val balanceInPoints: Double

    val holdPoints: Double

    val balanceInCommuns: Double

    val pageSize: Int

    val sendPointItems: LiveData<List<VersionedListItem>>

    val historyItems: LiveData<List<VersionedListItem>>

    val balance: List<WalletCommunityBalanceRecordDomain>

    val currentBalanceRecord: WalletCommunityBalanceRecordDomain

    suspend fun initBalance(needReload: Boolean)

    fun getCarouselStartData(): CarouselStartData

    fun switchBalanceRecord(communityId: CommunityIdDomain): Boolean

    suspend fun loadSendPointsPage()

    suspend fun retrySendPointsPage()

    suspend fun clearSendPoints()

    suspend fun loadHistoryPage()

    suspend fun retryHistoryPage()

    suspend fun clearHistory()
}