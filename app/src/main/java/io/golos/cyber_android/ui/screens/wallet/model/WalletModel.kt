package io.golos.cyber_android.ui.screens.wallet.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

interface WalletModel : ModelBase {
    val balance: List<WalletCommunityBalanceRecordDomain>

    val totalBalance: Double

    val pageSize: Int

    val sendPointItems: LiveData<List<VersionedListItem>>

    val historyItems: LiveData<List<VersionedListItem>>

    suspend fun initBalance(needReload: Boolean)

    suspend fun getMyPointsItems(): List<MyPointsListItem>

    suspend fun loadSendPointsPage()

    suspend fun retrySendPointsPage()

    suspend fun clearSendPoints()

    suspend fun loadHistoryPage()

    suspend fun retryHistoryPage()

    suspend fun clearHistory()
}