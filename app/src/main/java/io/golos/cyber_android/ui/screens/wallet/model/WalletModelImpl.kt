package io.golos.cyber_android.ui.screens.wallet.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator.BalanceCalculator
import io.golos.cyber_android.ui.screens.wallet_shared.history.data_source.HistoryDataSource
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.persistence.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.utils.id.IdUtil
import kotlinx.coroutines.flow.Flow
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
    private val historyDataSource: HistoryDataSource,
    private val balanceCalculator: BalanceCalculator,
    private val keyValueStorageFacade: KeyValueStorageFacade,
    private val globalSettingsRepository: GlobalSettingsRepository
) : ModelBaseImpl(),
    WalletModel {

    companion object{
        const val PREF_BALANCE_CURRENCY_COEFFICIENT = "PREF_BALANCE_CURRENCY_COEFFICIENT"
        private const val PREF_SHOW_HIDE_EMPTY_BALANCES = "PREF_SHOW_HIDE_EMPTY_BALANCES"
    }

    override lateinit var balance: List<WalletCommunityBalanceRecordDomain>

    override val balanceCurrency: Currencies
        get() = getCurrency()

    override val totalBalance: Double
        get() = (balanceCalculator.getTotalBalance(balance) * getCurrency().coefficient)

    override val sendPointItems: LiveData<List<VersionedListItem>>
        get() = sendPointsDataSource.items

    override val historyItems: LiveData<List<VersionedListItem>>
        get() = historyDataSource.items

    override val isBalanceUpdated: Flow<Boolean?>
        get() = globalSettingsRepository.isBalanceUpdated

    override suspend fun clearBalanceUpdateLastCallback() {
        globalSettingsRepository.notifyBalanceUpdate(null)
    }

    override suspend fun initBalance(needReload: Boolean) {
        balance = if(needReload) {
            walletRepository.getBalance()
        } else {
            sourceBalance
        }
    }

    override suspend fun getMyPointsItems(): List<MyPointsListItem> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            val communItem = balance.firstOrNull { it.communityId.code == GlobalConstants.COMMUN_CODE }
                ?: WalletCommunityBalanceRecordDomain(0.0, null, null, null, CommunityIdDomain(GlobalConstants.COMMUN_CODE), null, null)

            val result = mutableListOf<MyPointsListItem>()

            result.add(MyPointsListItem(IdUtil.generateLongId(), 0, false, false, true, communItem))

            result.addAll(
                balance
                    .filter { it.communityId.code != GlobalConstants.COMMUN_CODE }
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

    override suspend fun saveBalanceCurrency(currency: Currencies) {
        if(getCurrency() != currency) {
            saveCurrency(currency)
            initBalance(true)
        }
    }

    private fun getCurrency(): Currencies {
        var result = keyValueStorageFacade.getCurrencyCoefficient()?.let {  Currencies.getCurrency(it) }
        if(result == null) {
            saveCurrency(Currencies.COMMUN)
            result = Currencies.COMMUN
        }
        return result
    }

    private fun saveCurrency(currency: Currencies){
        keyValueStorageFacade.saveCurrencyCoefficient(currency.currencyName)
    }


    override suspend fun toggleShowHideEmptyBalances(isShow: Boolean) {
        saveShowHideEmptyBalancesState(isShow)
    }

    override fun getEmptyBalanceVisibility(): Boolean {
        return keyValueStorageFacade.areEmptyBalancesVisibility()
    }

    private fun saveShowHideEmptyBalancesState(isShow: Boolean){
        keyValueStorageFacade.saveEmptyBalancesVisibility(isShow)
    }
}