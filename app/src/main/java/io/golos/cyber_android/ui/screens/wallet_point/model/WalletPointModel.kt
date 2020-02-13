package io.golos.cyber_android.ui.screens.wallet_point.model
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

interface WalletPointModel : ModelBase {
    val title: String?

    val balanceInPoints: Double

    val holdPoints: Double

    val balanceInCommuns: Double

    val pageSize: Int

    val sendPointItems: LiveData<List<VersionedListItem>>

    val historyItems: LiveData<List<VersionedListItem>>

    suspend fun initBalance(needReload: Boolean)

    fun getCarouselStartData(): CarouselStartData

    fun switchBalanceRecord(communityId: String): Boolean

    suspend fun loadSendPointsPage()

    suspend fun retrySendPointsPage()

    suspend fun clearSendPoints()

    suspend fun loadHistoryPage()

    suspend fun retryHistoryPage()

    suspend fun clearHistory()
}