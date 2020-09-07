package io.golos.cyber_android.ui.screens.wallet.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import kotlinx.coroutines.flow.Flow
import io.golos.domain.dto.HistoryFilterDomain

interface WalletModel : ModelBase {
    val balance: List<WalletCommunityBalanceRecordDomain>

    val totalBalance: Double

    val balanceCurrency: Currencies

    val pageSize: Int

    val isBalanceUpdated: Flow<Boolean?>

    val sendPointItems: LiveData<List<VersionedListItem>>

    val historyItems: LiveData<List<VersionedListItem>>

    suspend fun initBalance(needReload: Boolean)

    suspend fun saveBalanceCurrency(currency: Currencies)

    suspend fun getMyPointsItems(): List<MyPointsListItem>

    suspend fun loadSendPointsPage()

    suspend fun notifyCurrencyUpdate(isCurrencyUpdated:Boolean)

    suspend fun clearBalanceUpdateLastCallback()

    suspend fun retrySendPointsPage()

    suspend fun clearSendPoints()

    suspend fun loadHistoryPage()

    suspend fun retryHistoryPage()

    suspend fun clearHistory()

    suspend fun toggleShowHideEmptyBalances(isShow: Boolean)

    fun getEmptyBalanceVisibility(): Boolean

    suspend fun applyFilters(historyFilterDomain: HistoryFilterDomain?)

}